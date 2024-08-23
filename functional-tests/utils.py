import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json

def generate_visualizations(model_level_json, class_level_json):
    # Aggregate metrics
    try:
        aggregate_model_precision = model_level_json["aggregate_model_precision"] if "aggregate_model_precision" in model_level_json else None
        aggregate_model_recall = model_level_json["aggregate_model_recall"] if "aggregate_model_recall" in model_level_json else None
        aggregate_model_f1_score = model_level_json["aggregate_model_f1_score"] if "aggregate_model_f1_score" in model_level_json else None
        semantic_similarity = model_level_json["semantic_similarity"] if "semantic_similarity" in model_level_json else None
        ragas_similarity = model_level_json["ragas_similarity"] if "ragas_similarity" in model_level_json else None
        keys= []
        values = []
        if aggregate_model_precision:
            keys.append("precision")
            values.append(aggregate_model_precision)

        if aggregate_model_recall:
            keys.append("recall")
            values.append(aggregate_model_recall)

        if aggregate_model_f1_score:
            keys.append("f1")
            values.append(aggregate_model_f1_score)

        if semantic_similarity:
            keys.append("semantic_similarity")
            values.append(semantic_similarity)

        if ragas_similarity:
            keys.append("ragas")
            values.append(ragas_similarity)

        bar_df = pd.DataFrame({'x': keys, 'y': values})
        fig = px.bar(bar_df, x='x', y='y', title='Model Level Metrics')
        st.plotly_chart(fig)
    except Exception as e:
        print(e)

    colors = ['green', 'grey', 'red']

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["classes_tp"],
            model_level_json["classes_fp"],
            model_level_json["classes_fn"]
        ]})
        if len(pie_df.values) > 0:
            pie = px.pie(pie_df, values='values', names='element', title='Classes')
            pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
            st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["attributes_tp"],
            model_level_json["attributes_fp"],
            model_level_json["attributes_fn"]
        ]})
        if len(pie_df.values) > 0:
            pie = px.pie(pie_df, values='values', names='element', title='Attributes')
            pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
            st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["operations_tp"],
            model_level_json["operations_fp"],
            model_level_json["operations_fn"]
        ]})
        if len(pie_df.values) > 0:
            pie = px.pie(pie_df, values='values', names='element', title='Operations')
            pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
            st.plotly_chart(pie)
    except Exception as e:
        print(e)


    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["references_tp"],
            model_level_json["references_fp"],
            model_level_json["references_fn"]
        ]})
        if len(pie_df.values) > 0:
            pie = px.pie(pie_df, values='values', names='element', title='References')
            pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
            st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["superTypes_tp"],
            model_level_json["superTypes_fp"],
            model_level_json["superTypes_fn"]
        ]})
        if len(pie_df.values) > 0:
            pie = px.pie(pie_df, values='values', names='element', title=f'Supertypes')
            pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
            st.plotly_chart(pie)
    except Exception as e:
        print(e)

    st.write("Matched Classes Metrics")
    st.dataframe(pd.DataFrame(class_level_json).T)