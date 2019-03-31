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
    STORAGE_PATH=gs://$BUCKET_NAME/
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
    TRAIN_DATA=gs://$BUCKET_NAME/Data
    EVAL_DATA=gs://$BUCKET_NAME/Data
    ```
  - Select the runtime version that supports the latest versions of the machine learning framework and other packages. To submit a training job with `Python 3.5`, set the Python version to `3.5` and the runtime version to `1.4` or greater. See the details of each version in the [Cloud ML Engine version list](https://cloud.google.com/ml-engine/docs/tensorflow/runtime-version-list) and [Specify Python version for a training job](https://cloud.google.com/ml-engine/docs/tensorflow/versioning#set-python-version-training)
    ```
    RUNTIME_VERSION=1.13 # to get python3
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

5. Run a local training job

>A local training job loads your Python training program and starts a training process in an environment that's similar to that of a live Cloud ML Engine cloud training job.

  - Specify an output directory and set a `MODEL_DIR` variable
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
      --train-files $TRAIN_DATA/aapl.us.txt \
      --eval-files $EVAL_DATA/aapl.us.txt \
      --num-epochs 10 \
      --num-samples 10
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
    --train-files $TRAIN_DATA/aapl.us.txt \
    --eval-files $EVAL_DATA/aapl.us.txt \
    --num-epochs 10 \
    --num-samples 10
    ```
    See `task.py` for all the command-line arguments.

  Monitor the progress of the training job by watching the command-line output or in **ML Engine > Jobs** on [Google Cloud Platform Console](https://console.cloud.google.com/mlengine/jobs?). More details in [Monitor Training page](https://cloud.google.com/ml-engine/docs/tensorflow/monitor-training).

7. Before we deploy and get prediction, we need to [export the model to a SavedModel](https://www.tensorflow.org/programmers_guide/saved_model#performing_the_export). A `saved_model` folder is created that save variables and execution graph each run.

  - The following command shows all available `SignatureDef` keys in a `MetaGraphDef`:
    ```
    saved_model_cli show --dir /tmp/<saved_model_dir> --tag_set serve
    ```
    where `saved_model_dir` is the folder, for example: `run_20190322-121359`

  - To show all available information in the `SavedModel`, run:
    ```
    saved_model_cli show \
    --dir /pred_output/<saved_model_dir> \
    --all
    ```

  - Invoke the `run` command to run a graph computation, passing inputs and then displaying (and optionally saving) the outputs. Here's the syntax:
    ```
    saved_model_cli run \
    --dir pred_output/run_20190322-121359/ \
    --tag_set serve \
    --signature_def serving_default \
    --inputs 'aapl='$TRAIN_DATA'/aapl.us.txt' \
    --outdir /pred_output/output_dir
    ```
  - After we have a working `saved_model`, store the SavedModel in Cloud Storage by running:
      ```
      gsutil cp -r pred_output/run_20190326-194717/  gs://$BUCKET_NAME/
      ```
8. Create input file and [format the input for prediction](https://cloud.google.com/ml-engine/docs/tensorflow/online-predict#formatting_your_input_for_online_prediction). In this repo the input files are in `pred_request` folder:
    ```
    {"input_values": ["aapl"]}
    ```
  Note that the root tag has the be the same as the input tensor name
9. Test prediction locally by using [gcloud local prediction](https://cloud.google.com/sdk/gcloud/reference/ml-engine/local/predict)
  - Set up a few variables:
    ```
    JSON_INSTANCES=pred_request/example1.json
    MODEL_DIR=pred_output/run_20190326-145225/ # The SavedModel output from running trainer/model.py
    ```
  - Get prediction locally
    ```
    gcloud ml-engine local predict \
    --model-dir $MODEL_DIR \
    --json-instances $JSON_INSTANCES
    #--text-instances $TEXT_INSTANCES \ # If input file is a text file instead of JSON
    ```
11. Make request to gcloud for [online prediction](https://cloud.google.com/sdk/gcloud/reference/ml-engine/predict):
  - If needed, create the model that you are deploying a new version of. There are [multiple ways](https://cloud.google.com/ml-engine/docs/tensorflow/deploying-models#creating_a_model_version) to do so.
    ```
    gcloud ml-engine models create "stock_advisor"
    ```
  - Create a new version. `origin` is where the SavedModel is saved:
    ```
    gcloud ml-engine versions create "version1" --model "stock_advisor" --origin $STORAGE_PATH/run_20190326-194717 --runtime-version $RUNTIME_VERSION --python-version $PYTHON_VERSION
    ```
  - Get information about your new version:
    ```
    gcloud ml-engine versions describe "version1" --model "stock_advisor"
    ```
    The output should look something like this:
    ```
    createTime: '2019-03-27T00:14:30Z'
    deploymentUri: gs://fluid-mote-232300-mlengine/run_20190326-194717
    etag: aNdXFOtObrE=
    framework: TENSORFLOW
    isDefault: true
    machineType: mls1-c1-m2
    name: projects/fluid-mote-232300/models/stock_advisor/versions/version1
    pythonVersion: '3.5'
    runtimeVersion: '1.13'
    state: READY
    ```
  - Since we just created a model and version, let's create `MODEL_NAME` and `VERSION_NAME` variables:
    ```
    VERSION_NAME=version1
    MODEL_NAME=stock_advisor
    ```
  - Request prediction from the newly create model
    ```
    gcloud ml-engine predict --model $MODEL_NAME  \
                     --version $VERSION_NAME \
                     --json-instances $JSON_INSTANCES
    ```

# Screenshots
<img width="300" alt="1" src="https://user-images.githubusercontent.com/20506541/55293526-5fa7b580-53c5-11e9-823c-b996d741f93e.png">
<img width="301" alt="2" src="https://user-images.githubusercontent.com/20506541/55293532-7948fd00-53c5-11e9-8ef0-b6462281ed37.png">
<img width="299" alt="3" src="https://user-images.githubusercontent.com/20506541/55293537-80700b00-53c5-11e9-863b-7f5f95773d7a.png">
