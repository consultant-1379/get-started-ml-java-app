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

import joblib
from seldon_core.user_model import SeldonResponse

class telecomClass(object):

    def __init__(self):
        print("Initializing")
        # Here we use the format previously used to import the model, the columns and target from the file
        self.model, self.ref_cols, self.target = joblib.load("model.pkl")

    def predict(self, X, features_names=None):
        Y = self.model.predict(X)
        runtime_metrics = [{"type": "COUNTER", "key": "model_instance_counter", "value": len(X)}]
        return SeldonResponse(data=Y, metrics=runtime_metrics, tags=None)

    def metrics(self):
        print("Returning metrics:")
        return [
        # a counter which will increase by the given value
        {"type": "COUNTER", "key": "model_counter", "value": 1},

        # a gauge which will be set to given value
        {"type": "GAUGE", "key": "model_gauge", "value": 100},

        # a timer (in msecs) which  will be aggregated into HISTOGRAM
        {"type": "TIMER", "key": "model_timer", "value": 20.2},
    ]
