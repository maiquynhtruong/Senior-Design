# Senior-Design
Senior Design class project (Fall 2018 and Spring 2019)

# Tentative Project Background Description

Our project is based on the investment finance sector. We use Tensorflow machine learning models to give make approximate prediction of stocks' opening prices the next day. The main interface will be a mobile app (Android so far) that users can download and provide their own stock predictions. The user will give two prediction: one with the ML model’s prediction input taken into account and another without. The next day, when real stock prices are available, user can compare the two predictions made yesterday with real values. The point of the app is to help beginners in stock investment practice their decision-making skills. It is often costly to make bad investment based on simple speculation without any machine’s input. We hope by using the app, users will be able to improve their weaknesses.

# Members
- [Yue Chang](https://github.com/changy66)
- [Mai Truong](https://github.com/maiquynhtruong/)

# How to run
The steps are very much based on Cloud ML Engine for Tensorflow [Getting Started tutorial](https://cloud.google.com/ml-engine/docs/tensorflow/getting-started-training-prediction) and `Training Models` and `Getting Predictions` sections of [Cloud ML Engine how-to guides](https://cloud.google.com/ml-engine/docs/tensorflow/how-tos).

##
1. Set up environment by installing `python`, `pip`, `virtualenv`, `tensorflow`
2. Set up Cloud Storage Bucket and upload training data
  - Specify a name for your new bucket
    ```
    PROJECT_ID=$(gcloud config list project --format "value(core.project)") # core.project is the project ID
    BUCKET_NAME=${PROJECT_ID}-mlengine
    ```
  - Select a region for your bucket and set a `REGION` environment variable
    ```
    REGION=us-central1 # or us-east1
    ```
  - If a bucket is already created, go to [GCP Storage console](https://console.cloud.google.com/storage/browser?) to view it. If not:
    - Create the new bucket
      ```
      gsutil mb -l $REGION gs://$BUCKET_NAME
      ```
    - Use gsutil to copy data files to your Cloud Storage bucket (takes a few minutes depending on the data and connection)
      ```
      gsutil cp -r Data gs://$BUCKET_NAME/Data # 'Data' is the data folder
      ```
  - Set the `TRAIN_DATA` and `EVAL_DATA` variables to point to the files
    ```
    TRAIN_DATA=gs://$BUCKET_NAME/Data/
    EVAL_DATA=gs://$BUCKET_NAME/Data/
    ```
  - Select the runtime version that supports the latest versions of the machine learning framework and other packages. To submit a training job with `Python 3.5`, set the Python version to `3.5` and the runtime version to `1.4` or greater. See the details of each version in the [Cloud ML Engine version list](https://cloud.google.com/ml-engine/docs/tensorflow/runtime-version-list) and [Specify Python version for a training job](https://cloud.google.com/ml-engine/docs/tensorflow/versioning#set-python-version-training)
    ```
    RUNTIME_VERSION=1.4 # to get python3
    PYTHON_VERSION=3.5
    ```
3. Set up environment variables
  - Give a name for the job, **must be unique for each run**
    ```
    now=$(date +"%Y%m%d_%H%M%S")
    JOB_NAME=stock_advisor_$now # or something else
    ```
  - Specify a directory for training job's output files. It's a good practice to use the job name as the output directory.
    ```
    OUTPUT_PATH=gs://$BUCKET_NAME/$JOB_NAME
    ```
  - Other variables
    ```
    TRAINER_PACKAGE_PATH="trainer/"
    MAIN_TRAINER_MODULE="trainer.task"
    PACKAGE_STAGING_PATH="gs://$BUCKET_NAME"

    ```

4. Building your package manually (to confirm with the recommended project structure)
  - Create `setup.py`
  - When you make your application into a Python package, you create a namespace. For example, if you create a package named `trainer`, and your main module is called `task.py`, you specify that package with the name `trainer.task`.

5. (Optional) Run a local training job

>A local training job loads your Python training program and starts a training process in an environment that's similar to that of a live Cloud ML Engine cloud training job.

  - Specify an output directory and set a MODEL_DIR variable
  ```
  MODEL_DIR=output
  ```
  - Delete the contents of the output directory in case data remains from a previous training run.
  ```
  rm -rf $MODEL_DIR/*
  ```
  - Run your training locally:
  ```
  gcloud ml-engine local train \
    --module-name trainer.task \
    --package-path trainer/ \
    --job-dir $MODEL_DIR \
    -- \
    --train-files $TRAIN_DATA \
    --eval-files $EVAL_DATA \
    --train-steps 1000 \
    --eval-steps 100
  ```


6. To run a single-instance training job in the cloud, package the application and submit training job (at the same time)
    ```
    gcloud ml-engine jobs submit training $JOB_NAME \
    --staging-bucket $PACKAGE_STAGING_PATH \
    --job-dir $OUTPUT_PATH  \
    --package-path $TRAINER_PACKAGE_PATH \
    --module-name $MAIN_TRAINER_MODULE \
    --python-version $PYTHON_VERSION \
    --region $REGION \
    --runtime-version $RUNTIME_VERSION \
    -- \
    --train-files $TRAIN_DATA \
    --eval-files $EVAL_DATA \
    --train-steps 1000 \
    --eval-steps 100 \
    --verbosity DEBUG
    ```
`verbosity` can be omitted.

  Monitor the progress of the training job by watching the command-line output or in **ML Engine > Jobs** on [Google Cloud Platform Console](https://console.cloud.google.com/mlengine/jobs?). More details in [Monitor Training page](https://cloud.google.com/ml-engine/docs/tensorflow/monitor-training).

7. Next step is to deploy a model. There's a separate page about [deploying models](https://cloud.google.com/ml-engine/docs/tensorflow/deploying-models).
