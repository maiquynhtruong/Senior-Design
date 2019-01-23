import tensorflow as tf
import os
import skimage
import numpy as np
import matplotlib as mpl
mpl.use('TkAgg')
import matplotlib.pyplot as plt

config=tf.ConfigProto(log_device_placement=True)
config=tf.ConfigProto(allow_soft_placement=True)

def load_data(data_directory):
    directories = [d for d in os.listdir(data_directory)
                    if os.path.isdir(os.path.join(data_directory, d))]
    labels = []
    images = []
    for d in directories:
        label_directory = os.path.join(data_directory, d)
        file_names = [os.path.join(label_directory, f)
                        for f in os.listdir(label_directory)
                        if f.endswith(".ppm")]
        for f in file_names:
            images.append(skimage.data.imread(f))
            labels.append(int(d))
    return images, labels

ROOT_PATH = ""
train_data_directory = os.path.join(ROOT_PATH, "Training")
test_data_directory = os.path.join(ROOT_PATH, "Testing")

images, labels = load_data(train_data_directory)
#
# with tf.Session() as sess:
#     output = sess.run(result)
#     print(output)

unique_labels = set(labels)
plt.figure(figsize=(15, 15))
i = 1
for label in unique_labels:
    image = images[labels.index(label)]
    plt.subplot(8, 8, i)
    plt.title("Label {0} ({1})".format(label, labels.count(label)))
    plt.axis('off')
    i += 1
    plt.imshow(image)
    plt.subplots_adjust(wspace=0.5)
plt.show()
