import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json

# Sample data
df = pd.read_json("output/model_level_json.json")

st.title('Dashboard')

try:
	bar_df = pd.DataFrame({'x': [
     "precision", 
     "recall", 
     "f1_score", 
     "cosine_similarity_tfidf", 
     "cosine_similarity_word2vec",
     "ragas_answer_similarity"
	], 'y': [
		df.T["aggregate_model_precision"][0], 
		df.T["aggregate_model_recall"][0], 
		df.T["aggregate_model_f1_score"][0],
		df.T["cosine_similarity_tfidf"][0],
		df.T["cosine_similarity_word2vec"][0],
		df.T["ragas_answer_similarity"][0] if df.T["ragas_answer_similarity"][0] > 0 else 0 
  	]
	})
	fig = px.bar(bar_df, x='x', y='y', title='Model Level Metrics')
	st.plotly_chart(fig)
except Exception as e:
    print(e)

colors = ['green', 'grey', 'red']

try:
	pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
		df.T["classes_tp"][0],
		df.T["classes_fp"][0],
		df.T["classes_fn"][0]
	]})
	pie = px.pie(pie_df, values='values', names='element', title='Classes')
	pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
	st.plotly_chart(pie)
except Exception as e:
    print(e)

try:
	pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
		df.T["attributes_tp"][0],
		df.T["attributes_fp"][0],
		df.T["attributes_fn"][0]
	]})
	pie = px.pie(pie_df, values='values', names='element', title='Attributes')
	pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
	st.plotly_chart(pie)
except Exception as e:
    print(e)

try:
	pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
		df.T["operations_tp"][0],
		df.T["operations_fp"][0],
		df.T["operations_fn"][0]
	]})
	pie = px.pie(pie_df, values='values', names='element', title='Operations')
	pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
	st.plotly_chart(pie)
except Exception as e:
    print(e)


try:
	pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
		df.T["references_tp"][0],
		df.T["references_fp"][0],
		df.T["references_fn"][0]
	]})
	pie = px.pie(pie_df, values='values', names='element', title='References')
	pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
	st.plotly_chart(pie)
except Exception as e:
    print(e)

try:
	pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
		df.T["superTypes_tp"][0],
		df.T["superTypes_fp"][0],
		df.T["superTypes_fn"][0]
	]})
	pie = px.pie(pie_df, values='values', names='element', title=f'Supertypes')
	pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
	st.plotly_chart(pie)
except Exception as e:
    print(e)

with open("output/class_level_json.json", "r") as cj:
    class_level_json = json.loads(cj.read())
    columns = [
        "name1", 
        "name2", 
        "attributes_tp",
        "attributes_fp",
        "attributes_fn",
        "references_tp",
        "references_fp",
        "references_fn",
        "superTypes_tp",
        "superTypes_fp",
        "superTypes_fn",
        "operations_tp",
        "operations_fp",
        "operations_fn",
    ]
    values = []
    for i in class_level_json:
        names1 = []
        names2 = []
        attributes_tp = []
        attributes_fp = []
        attributes_fn = []
        references_tp = []
        references_fp = []
        references_fn = []
        superTypes_tp = []
        superTypes_fp = []
        superTypes_fn = []
        operations_tp = []
        operations_fp = []
        operations_fn = []
        for class_obj in class_level_json[i]:
            names1.append(class_level_json[i][class_obj]["class_name_model1"])
            names2.append(class_level_json[i][class_obj]["class_name_model2"])
            attributes_tp.append(class_level_json[i][class_obj]["attributes_tp"])
            attributes_fp.append(class_level_json[i][class_obj]["attributes_fp"])
            attributes_fn.append(class_level_json[i][class_obj]["attributes_fn"])
            references_tp.append(class_level_json[i][class_obj]["references_tp"])
            references_fp.append(class_level_json[i][class_obj]["references_fp"])
            references_fn.append(class_level_json[i][class_obj]["references_fn"])
            operations_tp.append(class_level_json[i][class_obj]["operations_tp"])
            operations_fp.append(class_level_json[i][class_obj]["operations_fp"])
            operations_fn.append(class_level_json[i][class_obj]["operations_fn"])
            superTypes_tp.append(class_level_json[i][class_obj]["superTypes_tp"])
            superTypes_fp.append(class_level_json[i][class_obj]["superTypes_fp"])
            superTypes_fn.append(class_level_json[i][class_obj]["superTypes_fn"])
        values.append(names1)
        values.append(names2)
        values.append(attributes_tp)
        values.append(attributes_fp)
        values.append(attributes_fn)
        values.append(references_tp)
        values.append(references_fp)
        values.append(references_fn)
        values.append(superTypes_tp)
        values.append(superTypes_fp)
        values.append(superTypes_fn)
        values.append(operations_tp)
        values.append(operations_fp)
        values.append(operations_fn)
    
    fig = go.Figure(data=[go.Table(
        header=dict(values=columns,
			line_color='darkslategray',
			fill_color='maroon',
			align='left'
		),
        cells=dict(values=values,
				line_color='darkslategray',
				fill_color='black',
				align='left'))])
    fig.update_layout(width=600, height=1000)
    st.plotly_chart(fig)
