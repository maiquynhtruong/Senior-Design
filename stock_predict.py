# Followed tutorial at https://www.datacamp.com/community/tutorials/lstm-python-stock-market
from pandas_datareader import data
import matplotlib as mpl
import matplotlib.pyplot as plt
import pandas as pd
import datetime as dt
import urllib.request, json
import os
import numpy as np
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler

df = pd.read_csv(os.path.join('Stocks','hpq.us.txt'),delimiter=',',usecols=['Date','Open','High','Low','Close'])
print('Loaded data from the Kaggle repository')

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
train_data = mid_prices[:11000]
test_data = mid_prices[11000:]

# scale the data to be in 0 to 1 range
scaler = MinMaxScaler()
train_data = train_data.reshape(-1,1)
test_data = test_data.reshape(-1,1)

# Train the Scaler with training data and smooth data
smoothing_window_size = 2500
for di in range(1, 10000, smoothing_window_size):
    scaler.fit(train_data[di:di+smoothing_window_size,:])
    train_data[di:di+smoothing_window_size,:] = scaler.transform(train_data[di:di+smoothing_window_size,:])

# Normalize the last bit of remaining data
scaler.fit(train_data[di+smoothing_window_size:,:])
train_data[di+smoothing_window_size:,:] = scaler.transform(train_data[di+smoothing_window_size:,:])

# Reshape both train and test data
train_data = train_data.reshape(-1)

# Normalize test data
test_data = scaler.transform(test_data).reshape(-1)


############## I don't understand this part ####################
# Now perform exponential moving average smoothing
# So the data will have a smoother curve than the original ragged data
EMA = 0.0
gamma = 0.1
for ti in range(11000):
  EMA = gamma*train_data[ti] + (1-gamma)*EMA
  train_data[ti] = EMA

# Used for visualization and test purposes
all_mid_data = np.concatenate([train_data,test_data],axis=0)

############## I don't understand this part ####################

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

plt.figure(figsize=(18,9))
plt.plot(range(df.shape[0]), all_mid_data, color='b', label='True')
plt.plot(range(0, N), run_avg_predictions, color='orange', label='Prediction')
# plt.xticks(range(0,df.shape[0],50),df['Date'].loc[::50],rotation=45)
plt.xlabel('Date')
plt.ylabel('Mid Price (High + Low)/2')
plt.legend(fontsize=18)
plt.show()
