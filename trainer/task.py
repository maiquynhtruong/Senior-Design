# Followed the code from https://github.com/GoogleCloudPlatform/cloudml-samples/blob/master/census/customestimator/trainer/task.py
import argparse
import os

import tensorflow as tf

import trainer.model as model
# import model

def train_and_evaluate(args):
    """Run the training and evaluate using the high level API."""
    train_input = model.input_fn(
      args.train_files,
    )

    train_data, test_data, all_mid_data = model.prepare_data(train_input)
    model.lstm_predict(train_data, all_mid_data, epochs=args.num_epochs, num_samples=args.num_samples)


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  # Input Arguments.
  parser.add_argument(
      '--train-files',
      help='GCS file or local paths to training data',
      nargs='+',
      default='gs://fluid-mote-232300-mlengine/Data/aapl.us.txt')
  parser.add_argument(
      '--eval-files',
      help='GCS file or local paths to evaluation data',
      nargs='+',
      default='gs://fluid-mote-232300-mlengine/Data/aapl.us.txt')
  parser.add_argument(
      '--job-dir',
      help='GCS location to write checkpoints and export models',
      default='/exported-model')
  parser.add_argument(
      '--num-epochs',
      help="""\
      Maximum number of training data epochs on which to train.
      If both --max-steps and --num-epochs are specified,
      the training job will run for --max-steps or --num-epochs,
      whichever occurs first. If unspecified will run for --max-steps.\
      """,
      type=int,
      default=50)
  parser.add_argument(
      '--train-batch-size',
      help='Batch size for training steps',
      type=int,
      default=40)
  parser.add_argument(
      '--num-samples',
      help='Number of samples to calculate the batch size based on the size of input',
      type=int,
      default=10)
  parser.add_argument(
      '--eval-batch-size',
      help='Batch size for evaluation steps',
      type=int,
      default=40)
  parser.add_argument(
      '--embedding-size',
      help='Number of embedding dimensions for categorical columns',
      default=8,
      type=int)
  parser.add_argument(
      '--learning-rate',
      help='Learning rate for the optimizer',
      default=0.1,
      type=float)
  parser.add_argument(
      '--first-layer-size',
      help='Number of nodes in the first layer of the DNN',
      default=100,
      type=int)
  parser.add_argument(
      '--num-layers',
      help='Number of layers in the DNN',
      default=4, type=int)
  parser.add_argument(
      '--scale-factor',
      help='How quickly should the size of the layers in the DNN decay',
      default=0.7,
      type=float)
  parser.add_argument(
      '--train-steps',
      help="""\
      Steps to run the training job for. If --num-epochs is not specified,
      this must be. Otherwise the training job will run indefinitely.\
      """,
      default=100,
      type=int)
  parser.add_argument(
      '--eval-steps',
      help="""\
      Number of steps to run evalution for at each checkpoint.
      If unspecified will run until the input from --eval-files is exhausted""",
      default=None,
      type=int)
  parser.add_argument(
      '--export-format',
      help='The input format of the exported SavedModel binary',
      choices=['JSON', 'CSV', 'EXAMPLE'],
      default='JSON')
  parser.add_argument(
      '--verbosity',
      choices=['DEBUG', 'ERROR', 'FATAL', 'INFO', 'WARN'],
      default='INFO',
      help='Set logging verbosity')

  args, _ = parser.parse_known_args()

  # Set python level verbosity.
  tf.logging.set_verbosity(args.verbosity)
  # Set C++ Graph Execution level verbosity.
  os.environ['TF_CPP_MIN_LOG_LEVEL'] = str(
      tf.logging.__dict__[args.verbosity] / 10)

  # Run the training job.
  train_and_evaluate(args)
