#
# COPYRIGHT Ericsson 2023
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

# Seldon required python version 3.8
FROM python:3.8 as builder

WORKDIR /app

RUN pip install --no-cache-dir \
      joblib \
      matplotlib \
      pandas \
      scikit-learn

COPY modelTrain.py telecom_data.csv /app/

RUN python modelTrain.py

# Seldon required python version 3.8
FROM python:3.8 as model
WORKDIR /app

# Install python packages
COPY requirements.txt requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY /app /app
COPY --from=builder /app/model.pkl /app/

# Port for GRPC
EXPOSE 5000
# Port for REST
EXPOSE 9000

# Define environment variables
ENV MODEL_NAME telecomClass
ENV SERVICE_TYPE MODEL

# Changing folder to Seldon's default user
RUN chown -R 8888 /app

# CMD catatonit -- /opt/stdout-redirect -format =json -redirect=file -size=2 -rotate=5 -logfile=/logs/model.log -- "seldon-core-microservice" $MODEL_NAME --service-type $SERVICE_TYPE
CMD exec seldon-core-microservice $MODEL_NAME --service-type $SERVICE_TYPE
