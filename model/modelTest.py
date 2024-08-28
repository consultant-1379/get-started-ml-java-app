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

from telecomClass import telecomClass
import pandas as pd
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score



# Load the dataset
data = pd.read_csv('telecom_data.csv')
# Take a sample of 40
dataSample = data.sample(40)
# X will have all columns except the last one of the dataframe
X = dataSample.iloc[:, :-1]
# Y will have only the last column of the dataframe
Y = dataSample.iloc[:, -1:]

# We use the predict function from the telecomClass file previously created
predict = telecomClass().predict(X)
mse = mean_squared_error(Y, predict)
mae = mean_absolute_error(Y, predict)
r2 = r2_score(Y, predict)

print("Mean Squared Error:", mse)
print("Mean Absolute Error:", mae)
print("R-squared:", r2)
