import json
import pandas as pd
import os
from adapter import Adapter
from constants import CONSTANTS
from report import create_report

class Utils:
    def reorder_model_level_df(self, model_level_df):
        desired_order_model_level = CONSTANTS.MODEL_LEVEL_COLUMNS_ORDER.value
        model_level_df = model_level_df.reindex(
            columns=desired_order_model_level)
        return model_level_df

    def reorder_class_level_df(self, class_level_df):
        desired_order_class_level = CONSTANTS.CLASS_LEVEL_ORDER.value
        class_level_df = class_level_df.reindex(
            columns=desired_order_class_level)
        return class_level_df

    def rename_model_level_columns(self, model_df):
        names = CONSTANTS.MODEL_LEVEL_RENAMING.value
        model_df = model_df.rename(columns=names)
        return model_df

    def rename_class_level_columns(self, class_level_df):
        names = CONSTANTS.CLASS_LEVEL_RENAMING.value
        class_level_df = class_level_df.rename(columns=names)
        return class_level_df