import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json
import plotly.graph_objects as go


def get_venn_figure(onlyInModel1, onlyInModel2, intersection):
    fig = go.Figure()

    # Create scatter trace of text labels
    fig.add_trace(go.Scatter(
        x=[1, 1.6, 2.25],
        y=[1, 1, 1],
        text=[f'{onlyInModel1}', f'{intersection}', f'{onlyInModel2}'],
        mode="text",
        textfont=dict(
            color="white",
            size=18,
            family="Arail",
        )
    ))

    # Update axes properties
    fig.update_xaxes(
        showticklabels=False,
        showgrid=False,
        zeroline=False,
    )

    fig.update_yaxes(
        showticklabels=False,
        showgrid=False,
        zeroline=False,
    )

    # Add circles
    fig.add_shape(type="circle",
        line_color="red", fillcolor="red",
        x0=0, y0=0, x1=2, y1=2,
        label=dict(text="Base",textposition="top left")
    )
    fig.add_shape(type="circle",
        line_color="green", fillcolor="green",
        x0=1.25, y0=0, x1=3.25, y1=2,
        label=dict(text="Predicted",textposition="top right")
    )
    fig.update_shapes(opacity=0.5, xref="x", yref="y")

    fig.update_layout(
        height=375, width=300,
    )
    return fig

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
    
    col1, col2 = st.columns(2)

    with col1:
        try:
            pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
                model_level_json["classes_tp"],
                model_level_json["classes_fp"],
                model_level_json["classes_fn"]
            ]})
            if len(pie_df.values) > 0:
                st.write("Classes")
                fig = get_venn_figure(model_level_json["classes_fn"], model_level_json["classes_fp"], model_level_json["classes_tp"])
                st.plotly_chart(fig)
        except Exception as e:
            print(e)

        try:
            pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
                model_level_json["attributes_tp"],
                model_level_json["attributes_fp"],
                model_level_json["attributes_fn"]
            ]})
            if len(pie_df.values) > 0:
                st.write("Attributes")
                fig = get_venn_figure(model_level_json["attributes_fn"], model_level_json["attributes_fp"], model_level_json["attributes_tp"])
                st.plotly_chart(fig)
        except Exception as e:
            print(e)

    with col2:
        try:
            pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
                model_level_json["operations_tp"],
                model_level_json["operations_fp"],
                model_level_json["operations_fn"]
            ]})
            if len(pie_df.values) > 0:
                st.write("Operations")
                fig = get_venn_figure(model_level_json["operations_fn"], model_level_json["operations_fp"], model_level_json["operations_tp"])
                st.plotly_chart(fig)
        except Exception as e:
            print(e)


        try:
            pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
                model_level_json["references_tp"],
                model_level_json["references_fp"],
                model_level_json["references_fn"]
            ]})
            if len(pie_df.values) > 0:
                st.write("References")
                fig = get_venn_figure(model_level_json["references_fn"], model_level_json["references_fp"], model_level_json["references_tp"])
                st.plotly_chart(fig)
        except Exception as e:
            print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["superTypes_tp"],
            model_level_json["superTypes_fp"],
            model_level_json["superTypes_fn"]
        ]})
        if len(pie_df.values) > 0:
            st.write("Supertypes")
            fig = get_venn_figure(model_level_json["superTypes_fn"], model_level_json["superTypes_fp"], model_level_json["superTypes_tp"])
            st.plotly_chart(fig)
    except Exception as e:
        print(e)

    st.write("Matched Classes Metrics")
    st.dataframe(pd.DataFrame(class_level_json).T)
    
def summarize_results_of_bulk_comparison(array_of_model_level_json):
    results_json = {
        "base_model": [],
        "predicted_model": [],
        "SEMANTIC_SIMILARITY": [],
        "comparit_precision": [],
        "comparit_recall": [],
        "comparit_f1_score": [],
    }
 
    for model_level_json in array_of_model_level_json:
        try:
            results_json["base_model"].append(model_level_json["model1_identifier"])
        except KeyError as ke:
            results_json["base_model"].append(model_level_json["model1_identifier"])

        try:
            results_json["predicted_model"].append(model_level_json["model2_identifier"])
        except KeyError as ke:
            results_json["predicted_model"].append(model_level_json["model2_identifier"])

        try:
            results_json["comparit_precision"].append(model_level_json["aggregate_model_precision"])
        except KeyError as ke:
            results_json["comparit_precision"].append(-1)

        try:
            results_json["comparit_recall"].append(model_level_json["aggregate_model_recall"])
        except KeyError as ke:
            results_json["comparit_recall"].append(-1)

        try:
            results_json["comparit_f1_score"].append(model_level_json["aggregate_model_f1_score"])
        except KeyError as ke:
            results_json["comparit_f1_score"].append(-1)

        try:
            results_json["SEMANTIC_SIMILARITY"].append(model_level_json["semantic_similarity"])
        except KeyError as ke:
            results_json["SEMANTIC_SIMILARITY"].append(-1)

    df = pd.DataFrame(results_json)
    return df
    
    df = st.session_state.results_df
    fig = px.box(df, y=["comparit_f1_score", "comparit_precision", "comparit_recall", "SEMANTIC_SIMILARITY"], points="all")
    st.plotly_chart(fig)

    values = df["predicted_model"].values
    option = st.selectbox("Select Model Pair to view individual results", values, index=None)
    st.write(option)
    model_level_json = {}
    class_level_json = {}
    # with open(f'{option[0:option.rindex("/")]}/model_level_json.json', 'r') as fr:
    #     model_level_json = json.loads(fr.read())
    # with open(f'{option[0:option.rindex("/")]}/class_level_json.json', 'r') as fr:
    #     class_level_json = json.loads(fr.read())

    # col1, col2 = st.columns(2)
    # with col1:
    #     base_model = ""
    #     try:
    #         path = model_level_json["model1_identifier"]
    #         with open(path) as fr:
    #             base_model = fr.read()
    #     except Exception as e:
    #         base_model = "Model not found"
    #     st.text_area("base_model_ecore", base_model, height = 500)

    # with col2:
    #     path = model_level_json["model2_identifier"]
    #     with open(path) as fr:
    #         predicted_model = fr.read()
    #     st.text_area("predicted model ecore", predicted_model, height = 500)

    # col1, col2 = st.columns(2)
    # with col1:
    #     base_model = ""
    #     try:
    #         path = model_level_json["model1_identifier"][0:model_level_json["model1_identifier"].rindex(".")] + ".emf"
    #         with open(path) as fr:
    #             base_model = fr.read()
    #     except Exception as e:
    #         base_model = "Model not found"
    #     st.text_area("base_model_emfatic", base_model, height = 500)

    # with col2:
    #     path = model_level_json["model2_identifier"][0:model_level_json["model2_identifier"].rindex(".")] + ".emf"
    #     with open(path) as fr:
    #         predicted_model = fr.read()
    #     st.text_area("predicted model emfatic", predicted_model, height = 500)

    # generate_visualizations(model_level_json, class_level_json)        