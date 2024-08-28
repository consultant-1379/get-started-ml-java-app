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

import matplotlib.pyplot as plt
import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score

import joblib

# Load the dataset
data = pd.read_csv('telecom_data.csv')

# Assume 'X' contains your feature columns and 'y' contains your target column
X = data.iloc[:, :-1]
Y = data.iloc[:, -1:]

# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=0)

# Create a linear regression model
model = LinearRegression()

# Train the model on the training data
model.fit(X_train, y_train)

# Make predictions on the testing data
y_pred = model.predict(X_test)

# Calculate metrics
mse = mean_squared_error(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)

# Mean Squared Error (MSE): Measures the average squared difference between predicted and actual values.
print(f"Mean Squared Error: {mse}")
# Mean Absolute Error (MAE): Measures the average absolute difference between predicted and actual values.
print(f"Mean Absolute Error: {mae}")
# R-squared (Coefficient of Determination): Indicates the proportion of the variance
# in the dependent variable that's predictable from the independent variables.
print(f"R-squared: {r2}")

# Visualize predictions
plt.scatter(y_test, y_pred, color='blue')
plt.plot([min(y_test), max(y_test)], [min(y_test), max(y_test)], linestyle='--', color='red')

plt.xlabel('Actual Performance')
plt.ylabel('Predicted Performance')
plt.title('Actual vs. Predicted Performance')
plt.show()

# Exporting the model

# ref_cols is a list that contains the column names that will help identify the model
# during the export/import process. Target is a variable that has the target colmun's name
ref_cols = list(X.columns)
target = list(Y.columns)[0]

# We Pass the model, the list and the variable as a value parameter and name our model.
joblib.dump(value=[model, ref_cols, target], filename="model.pkl")

# The model is now exported and serilized and ready to be used.
