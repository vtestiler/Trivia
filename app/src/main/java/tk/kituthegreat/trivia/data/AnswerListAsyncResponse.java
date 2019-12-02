package tk.kituthegreat.trivia.data;

import java.util.ArrayList;

import tk.kituthegreat.trivia.model.Question;

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
