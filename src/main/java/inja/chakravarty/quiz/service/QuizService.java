package inja.chakravarty.quiz.service;

import inja.chakravarty.quiz.dao.QuestionDao;
import inja.chakravarty.quiz.dao.QuizDao;
import inja.chakravarty.quiz.model.Question;
import inja.chakravarty.quiz.model.QuestionWrapper;
import inja.chakravarty.quiz.model.Quiz;
import inja.chakravarty.quiz.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    private QuizDao quizDao;
    @Autowired
    private QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category,numQ);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuiz(int id) {
        Optional<Quiz> quiz = quizDao.findById(id);
        if(quiz.isPresent()){
            List<Question> questions = quiz.get().getQuestions();
            List<QuestionWrapper> questionWrappers = new ArrayList<>();
            for(Question question: questions){
                QuestionWrapper questionWrapper = new QuestionWrapper();
                questionWrapper.setId(question.getId());
                questionWrapper.setQuestionTitle(question.getQuestionTitle());
                questionWrapper.setOption1(question.getOption1());
                questionWrapper.setOption2(question.getOption2());
                questionWrapper.setOption3(question.getOption3());
                questionWrapper.setOption4(question.getOption4());
                questionWrappers.add(questionWrapper);
            }
            return new ResponseEntity<>(questionWrappers,HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Integer> getScore(int id, List<Response> responses) {
        Optional<Quiz> quiz = quizDao.findById(id);
        if(quiz.isPresent()){
            List<Question> questions = quiz.get().getQuestions();
            int right = 0 , i = 0;
            for(Response response:responses){
                if(response.getResponse().equalsIgnoreCase(questions.get(i).getRightAns()))
                    right++;
                i++;
            }
            return new ResponseEntity<>(right,HttpStatus.OK);
        }
        return new ResponseEntity<>(0,HttpStatus.BAD_REQUEST);
    }
}
