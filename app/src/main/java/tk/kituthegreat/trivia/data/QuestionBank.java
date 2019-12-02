package tk.kituthegreat.trivia.data;

import android.util.Log;
import android.view.textclassifier.TextLinks;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tk.kituthegreat.trivia.controller.AppController;
import tk.kituthegreat.trivia.model.Question;

import static tk.kituthegreat.trivia.controller.AppController.TAG;

public class QuestionBank {
    ArrayList<Question> questionArrayList= new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements.json";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        Question question = new Question();
                        question.setAnswer(response.getJSONArray(i).get(0).toString());
                        question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                        Log.d("JSON", "onResponse: " + question.isAnswerTrue());

                        // add question to the List
                        questionArrayList.add(question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(null != callBack) callBack.processFinished(questionArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        } );
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);


        return questionArrayList;
    }
}
