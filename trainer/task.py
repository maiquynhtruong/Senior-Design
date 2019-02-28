# Followed tutorial at https://www.datacamp.com/community/tutorials/lstm-python-stock-market
# from pandas_datareader import data
# import matplotlib as mpl
# import matplotlib.pyplot as plt
import pandas as pd
import datetime as dt
import urllib.request, json
import os
import numpy as np
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler

# df = pd.read_csv(os.path.join('../Data','appf.us.txt'),delimiter=',',usecols=['Date','Open','High','Low','Close'])
df = pd.read_csv(os.path.join('Data','appf.us.txt'),delimiter=',',usecols=['Date','Open','High','Low','Close'])

df = df.sort_values('Date')

# plt.figure(figsize = (18,9))
# plt.plot(range(df.shape[0]),(df['Low']+df['High'])/2.0)
# plt.xticks(range(0,df.shape[0],500),df['Date'].loc[::500],rotation=45)
# plt.xlabel('Date',fontsize=18)
# plt.ylabel('Mid Price',fontsize=18)
# plt.show()

# First calculate the mid prices from the highest and lowest
high_prices = df.loc[:,'High'].as_matrix()
low_prices = df.loc[:,'Low'].as_matrix()
mid_prices = (high_prices+low_prices)/2.0

# Split training and test data. 11000 entries for train data
data_len = len(mid_prices)
train_test_split = int(data_len*8/10) # 80 / 20 ratio for train and test
print('train_test_split= ', train_test_split)

train_data = mid_prices[:train_test_split]
test_data = mid_prices[train_test_split:]

# scale the data to be in 0 to 1 range
scaler = MinMaxScaler()
train_data = train_data.reshape(-1,1) #1 column matrix
test_data = test_data.reshape(-1,1)

# Train the Scaler with training data and smooth data
num_windows = 10
smoothing_window_size = int(train_test_split/num_windows)
for di in range(0, train_test_split, smoothing_window_size):
        # print('di=', di, '\nsmoothing_window_size=', smoothing_window_size, '\ndi+smoothing_window_size=', di+smoothing_window_size)
        scaler.fit(train_data[di:di+smoothing_window_size,:])
        train_data[di:di+smoothing_window_size,:] = scaler.transform(train_data[di:di+smoothing_window_size,:])

# Normalize the last bit of remaining data
# print('di=', di, '\nsmoothing_window_size=', smoothing_window_size, '\di+smoothing_window_size=', di+smoothing_window_size)
if train_data[di+smoothing_window_size:,:].size > 0:
    scaler.fit(train_data[di+smoothing_window_size:,:])
    train_data[di+smoothing_window_size:,:] = scaler.transform(train_data[di+smoothing_window_size:,:])

# Reshape both train and test data
train_data = train_data.reshape(-1)

# Normalize test data
test_data = scaler.transform(test_data).reshape(-1)


# Now perform exponential moving average smoothing
# So the data will have a smoother curve than the original ragged data
EMA = 0.0
gamma = 0.1
for ti in range(train_test_split):
  EMA = gamma*train_data[ti] + (1-gamma)*EMA
  train_data[ti] = EMA

# Used for visualization and test purposes
all_mid_data = np.concatenate([train_data,test_data],axis=0)

################### Standard Average ###################
# Predict future stock market prices as an average of the previously observed stock market prices within a fixed size window, say past 100 days
window_size = 100
N = train_data.size
std_avg_predictions = []
std_avg_x = []
mse_errors = []

for pred_idx in range(window_size, N):
    if pred_idx >= N:
        date = dt.datetime.strptime(k, '%Y-%m-%d').date() + dt.timedelta(days=1)
    else:
        date = df.loc[pred_idx, 'Date']

    std_avg_predictions.append(np.mean(train_data[pred_idx-window_size:pred_idx]))
    mse_errors.append((std_avg_predictions[-1]-train_data[pred_idx])**2)
    std_avg_x.append(date)

print('MSE error for standard averaging: %.5f'%(0.5*np.mean(mse_errors)))

# plt.figure(figsize=(18,9))
# plt.plot(range(df.shape[0]), all_mid_data, color='b', label='True')
# plt.plot(range(window_size, N), std_avg_predictions, color='orange', label='Prediction')
# plt.xticks(range(0,df.shape[0],50),df['Date'].loc[::50],rotation=45)
# plt.xlabel('Date')
# plt.ylabel('Mid Price (High + Low)/2')
# plt.legend(fontsize=18)
# plt.show()

################### Exponential Moving Average ###################

run_avg_predictions = []
run_avg_x = []
mse_errors = []
running_mean = 0.0 # Exponential Moving Average maintained over time
run_avg_predictions.append(running_mean)

# contribution of recent prediction to EMA. If recent value accounts for very small fraction, it allows to perserve
# much older values saw early on
decay = 0.5
for pred_idx in range(1, N):
    running_mean = decay*running_mean + (1-decay)*train_data[pred_idx-1]
    run_avg_predictions.append(running_mean)
    mse_errors.append((running_mean-train_data[pred_idx])**2)
    run_avg_x.append(date)

print('MSE error for EMA averaging: %.5f'%(0.5*np.mean(mse_errors)))

# plt.figure(figsize=(18,9))
# plt.plot(range(df.shape[0]), all_mid_data, color='b', label='True')
# plt.plot(range(0, N), run_avg_predictions, color='orange', label='Prediction')
# plt.xticks(range(0,df.shape[0],50),df['Date'].loc[::50],rotation=45)
# plt.xlabel('Date')
# plt.ylabel('Mid Price (High + Low)/2')
# plt.legend(fontsize=18)
# plt.show()

################### Stock prediction with LSTM ###################

class DataGeneratorSeq(object):
    def __init__(self, prices, batch_size, num_unroll):
        self._prices = prices
        self._prices_length = len(self._prices) - num_unroll
        self._batch_size = batch_size
        self._num_unroll = num_unroll
        self._segments = self._prices_length // self._batch_size
        self._cursor = [offset * self._segments for offset in range(self._batch_size)]

    def next_batch(self):
        batch_data = np.zeros(self._batch_size, dtype=np.float32)
        batch_labels = np.zeros(self._batch_size, dtype=np.float32)

        for b in range(self._batch_size):
            if self._cursor[b]+1 >= self._prices_length:
                self._cursor[b] = np.random.randint(0, (b+1)*self._segments)

            batch_data[b] = self._prices[self._cursor[b]]
            batch_labels[b] = self._prices[self._cursor[b]+np.random.randint(0,5)]

            self._cursor[b] = (self._cursor[b]+1) * self._prices_length

        return batch_data, batch_labels

    def unroll_batches(self):
        unroll_data, unroll_labels = [], []
        init_data, init_label = None, None
        for ui in range(self._num_unroll):
            data, label = self.next_batch()
            unroll_data.append(data)
            unroll_labels.append(label)

        return unroll_data, unroll_labels

    def reset_indices(self):
        for b in range(self._batch_size):
            self._cursor[b] = np.random.randint(0, min((b+1)*self._segments, self._prices_length-1))

data_gen = DataGeneratorSeq(train_data, 5, 5)
u_data, u_labels = data_gen.unroll_batches()

for ui,(data,label) in enumerate(zip(u_data, u_labels)):
    print('\n\nUnrolled index %d'%ui)
    data_ind = data
    label_ind = label
    print('\tInputs: ', data)
    print('\tOutputs: ', label)

## Defining hyperparameters

D = 1 # Dimensionality of the input, i.e. stock price
num_unrollings = 50 # How many contnuous time steps for a single optimization step
batch_size = int(train_data.size / 10) # number of samples in a batch
num_nodes = [200, 200, 150] # number of hidden nodes in each layer
n_layers = len(num_nodes) # number of layers
dropout = 0.5 # dropout amount

tf.reset_default_graph()

## Defining inputs and outputs

train_inputs, train_outputs = [], []

# Unroll the input over time defining placeholders for each time step
for ui in range(num_unrollings):
    train_inputs.append(tf.placeholder(tf.float32, shape=[batch_size,D], name='train_inputs_%d'%ui))
    train_outputs.append(tf.placeholder(tf.float32, shape=[batch_size,1], name='train_outputs_%d'%ui))

## Defining parameters of the LSTM and the regression layers

lstm_cells = [tf.contrib.rnn.LSTMCell(num_units=num_nodes[li],
                                    state_is_tuple=True,
                                    initializer=tf.contrib.layers.xavier_initializer())
    for li in range(n_layers)]
drop_lstm_cells = [tf.contrib.rnn.DropoutWrapper(cell=lstm,
                                                input_keep_prob=1.0,
                                                output_keep_prob=1.0-dropout,
                                                state_keep_prob=1.0-dropout
) for lstm in lstm_cells]

drop_multi_cell = tf.contrib.rnn.MultiRNNCell(drop_lstm_cells)
multi_cell = tf.contrib.rnn.MultiRNNCell(lstm_cells)

w = tf.get_variable(name='w', shape=[num_nodes[-1],1], initializer=tf.contrib.layers.xavier_initializer())
b = tf.get_variable(name='b', initializer=tf.random_uniform(shape=[1], minval=-0.1, maxval=0.1))

## Calculating LSTM output and Feeding it to the regression layer to get final prediction

c, h = [], [] # # cell state and hidden state variables to maintain the state of the LSTM
initial_state = []
for li in range(n_layers):
    c.append(tf.Variable(tf.zeros([batch_size, num_nodes[li]]), trainable=False))
    h.append(tf.Variable(tf.zeros([batch_size, num_nodes[li]]), trainable=False))
    initial_state.append(tf.contrib.rnn.LSTMStateTuple(c[li], h[li]))

# Do several tensor transofmations, because the function dynamic_rnn requires the output to be of
# a specific format. Read more at: https://www.tensorflow.org/api_docs/python/tf/nn/dynamic_rnn
all_inputs = tf.concat([tf.expand_dims(t,0) for t in train_inputs], axis=0)

# all_outputs is [seq_length, batch_size, num_nodes]
all_lstm_outputs, state = tf.nn.dynamic_rnn(drop_multi_cell, all_inputs,
                            initial_state=tuple(initial_state),
                            time_major=True, dtype=tf.float32)

all_lstm_outputs = tf.reshape(all_lstm_outputs, [batch_size*num_unrollings, num_nodes[-1]])

all_outputs = tf.nn.xw_plus_b(all_lstm_outputs, w, b) #calculate the LSTM outputs with the tf.nn.dynamic_rnn function
split_outputs = tf.split(value=all_outputs, num_or_size_splits=num_unrollings, axis=0) #  split the output back to a list of num_unrolling tensors

## Loss calculation and optimizer

# When calculating the loss you need to be careful about the exact form, because you calculate
# loss of all the unrolled steps at the same time
# Therefore, take the mean error or each batch and get the sum of that over all the unrolled steps
print('Defining training loss')
loss = 0.0
with tf.control_dependencies([tf.assign(c[li], state[li][0]) for li in range(n_layers)]+
                             [tf.assign(h[li], state[li][1]) for li in range(n_layers)]):
    for ui in range(num_unrollings):
        loss += tf.reduce_mean(0.5*(split_outputs[ui]-train_outputs[li])**2)

print('Learning rate decay operation')
global_step = tf.Variable(0, trainable=False)
inc_gstep = tf.assign(global_step, global_step+1)
tf_learning_rate = tf.placeholder(shape=None, dtype=tf.float32)
tf_min_learning_rate = tf.placeholder(shape=None, dtype=tf.float32)

learning_rate = tf.maximum(
    tf.train.exponential_decay(tf_learning_rate, global_step, decay_steps=1, decay_rate=0.5, staircase=True), tf_min_learning_rate
)

# Optimizer
print('Tf optimizer operation')
optimizer = tf.train.AdamOptimizer(learning_rate)
gradients, v = zip(*optimizer.compute_gradients(loss))
gradients, _ = tf.clip_by_global_norm(gradients, 5.0)
optimizer = optimizer.apply_gradients(zip(gradients, v))
print('\tAll done')

## Prediction Related Calculations
print('Defining prediction related to TF functions')
sample_inputs = tf.placeholder(tf.float32, shape=[1,D])

# Maintaining LSTM stage for prediction stage
sample_c, sample_h, initial_sample_state = [], [], []
for li in range(n_layers):
    sample_c.append(tf.Variable(tf.zeros([1, num_nodes[li]]), trainable=False))
    sample_h.append(tf.Variable(tf.zeros([1, num_nodes[li]]), trainable=False))
    initial_sample_state.append(tf.contrib.rnn.LSTMStateTuple(sample_c[li], sample_h[li]))

reset_sample_states = tf.group(*[tf.assign(sample_c[li], tf.zeros([1, num_nodes[li]])) for li in range(n_layers)],
                               *[tf.assign(sample_h[li], tf.zeros([1, num_nodes[li]])) for li in range(n_layers)])

sample_outputs, sample_state = tf.nn.dynamic_rnn(multi_cell, tf.expand_dims(sample_inputs, 0),
                                                initial_state=tuple(initial_sample_state),
                                                time_major=True,
                                                dtype=tf.float32)

with tf.control_dependencies([tf.assign(sample_c[li], sample_state[li][0]) for li in range(n_layers)] +
                             [tf.assign(sample_h[li], sample_state[li][1]) for li in range(n_layers)]):
    sample_prediction = tf.nn.xw_plus_b(tf.reshape(sample_outputs, [1,-1]), w, b)
print('\tAll done')

## Running the LSTMs
# train and predict stock price movements for several epochs and see whether the predictions get better or worse over time

epochs = 2
valid_summary = 1 # Interval you make test predictions

n_predict_once = 50 # Number of steps you continously predict for

train_seq_length = train_data.size # Full length of the training data

train_mse_ot = [] # Accumulate Train losses
test_mse_ot = [] # Accumulate Test loss
predictions_over_time = [] # Accumulate predictions

session = tf.InteractiveSession()

tf.global_variables_initializer().run()

# Used for decaying learning rate
loss_nondecrease_count = 0
loss_nondecrease_threshold = 2 # If the test error hasn't increased in this many steps, decrease learning rate

print('Initialized')
average_loss = 0

# Define data generator
data_gen = DataGeneratorSeq(train_data,batch_size,num_unrollings)

x_axis_seq = []

# Points you start our test predictions from
test_points_seq = np.arange(train_test_split, data_len,50).tolist()

mse_seq = [] # MSE of each epoch

for ep in range(epochs):
    print('Epoch {}'.format(ep))
    # ========================= Training =====================================
    for step in range(train_seq_length//batch_size):

        u_data, u_labels = data_gen.unroll_batches()

        feed_dict = {}
        for ui,(dat,lbl) in enumerate(zip(u_data,u_labels)):
            feed_dict[train_inputs[ui]] = dat.reshape(-1,1)
            feed_dict[train_outputs[ui]] = lbl.reshape(-1,1)

        feed_dict.update({tf_learning_rate: 0.0001, tf_min_learning_rate:0.000001})

        _, l = session.run([optimizer, loss], feed_dict=feed_dict)

        average_loss += l

     # ============================ Validation ==============================
    if (ep+1) % valid_summary == 0:

        average_loss = average_loss/(valid_summary*(train_seq_length//batch_size))

        # The average loss
        if (ep+1)%valid_summary==0:
            print('Average loss at step %d: %f' % (ep+1, average_loss))

        train_mse_ot.append(average_loss)

        average_loss = 0 # reset loss

        predictions_seq = []

        mse_test_loss_seq = []

      # ===================== Updating State and Making Predicitons ========================
        for w_i in test_points_seq:
            mse_test_loss = 0.0
            our_predictions = []

            if (ep+1)-valid_summary==0:
              # Only calculate x_axis values in the first validation epoch
              x_axis=[]

            # Feed in the recent past behavior of stock prices
            # to make predictions from that point onwards
            for tr_i in range(w_i-num_unrollings+1,w_i-1):
                current_price = all_mid_data[tr_i]
                feed_dict[sample_inputs] = np.array(current_price).reshape(1,1)
                _ = session.run(sample_prediction,feed_dict=feed_dict)

            feed_dict = {}

            current_price = all_mid_data[w_i-1]

            feed_dict[sample_inputs] = np.array(current_price).reshape(1,1)

             # Make predictions for this many steps
             # Each prediction uses previous prediciton as it's current input
            for pred_i in range(n_predict_once):
                if w_i + pred_i < data_len:

                    pred = session.run(sample_prediction,feed_dict=feed_dict)

                    our_predictions.append(np.asscalar(pred))

                    feed_dict[sample_inputs] = np.asarray(pred).reshape(-1,1)

                    if (ep+1)-valid_summary==0:
                    # Only calculate x_axis values in the first validation epoch
                        x_axis.append(w_i+pred_i)
                    mse_test_loss += 0.5*(pred-all_mid_data[w_i+pred_i])**2

            session.run(reset_sample_states)

            predictions_seq.append(np.array(our_predictions))

            mse_test_loss /= n_predict_once
            mse_test_loss_seq.append(mse_test_loss)

            if (ep+1)-valid_summary==0:
                x_axis_seq.append(x_axis)

        current_test_mse = np.mean(mse_test_loss_seq)

        # Learning rate decay logic
        if len(test_mse_ot)>0 and current_test_mse > min(test_mse_ot):
          loss_nondecrease_count += 1
        else:
          loss_nondecrease_count = 0

        if loss_nondecrease_count > loss_nondecrease_threshold :
            session.run(inc_gstep)
            loss_nondecrease_count = 0
            print('\tDecreasing learning rate by 0.5')

        test_mse_ot.append(current_test_mse)
        local_mse = np.mean(mse_test_loss_seq)
        mse_seq.append(local_mse)
        print('\tTest MSE: %.5f'%local_mse)
        predictions_over_time.append(predictions_seq)
        print('\tFinished Predictions')

best_prediction_epoch = mse_seq.index(min(mse_seq)) # replace this with the epoch that you got the best results when running the plotting code
print('best_prediction_epoch=', best_prediction_epoch)

# plt.figure(figsize = (18,18))
# plt.subplot(2,1,1)
# plt.plot(range(df.shape[0]),all_mid_data,color='b')

# Plotting how the predictions change over time
# Plot older predictions with low alpha and newer predictions with high alpha
start_alpha = 0.25
alpha  = np.arange(start_alpha,1.1,(1.0-start_alpha)/len(predictions_over_time[::3]))

for p_i, prediction in enumerate(predictions_over_time[::3]):
    for xval, yval in zip(x_axis_seq, prediction):
        plt.plot(xval, yval, color='r', alpha=alpha[p_i])

# plt.title('Evolution of Test Predictions Over Time',fontsize=18)
# plt.xlabel('Date',fontsize=18)
# plt.ylabel('Mid Price',fontsize=18)
# plt.xlim(train_test_split,data_len)
#
# plt.subplot(2,1,2)

# Predicting the best test prediction you got
# plt.plot(range(df.shape[0]),all_mid_data,color='b')
# for xval, yval in zip(x_axis_seq, predictions_over_time[best_prediction_epoch]):
#     plt.plot(xval,yval,color='r')
#
# plt.title('Best Test Predictions Over Time',fontsize=18)
# plt.xlabel('Date',fontsize=18)
# plt.ylabel('Mid Price',fontsize=18)
# plt.xlim(train_test_split,data_len)
# plt.show()
