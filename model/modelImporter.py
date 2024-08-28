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

import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import joblib
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score

# Load the dataset
data = pd.read_csv('telecom_data.csv')
dataSample = data.sample(40)
dataSample

# Load the model
model, ref_cols, target = joblib.load("model.pkl")

# Use the model to predict
X = dataSample[ref_cols]
Y = dataSample[target]
predict = model.predict(X)

# Calculate metrics
mse = mean_squared_error(Y, predict)
mae = mean_absolute_error(Y, predict)
r2 = r2_score(Y, predict)

print("Mean Squared Error:", mse)
print("Mean Absolute Error:", mae)
print("R-squared:", r2)