package com.javamentor.qa.platform.webapp.controllers.rest;

import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.model.AnswerService;
import com.javamentor.qa.platform.service.abstracts.model.ReputationService;
import com.javamentor.qa.platform.service.abstracts.model.UserService;
import com.javamentor.qa.platform.service.abstracts.model.VoteAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/user/question/{questionId}/answer")
@Tag("Данный контроллер обрабатывает входящие запросы обработки ответов.")
public class ResourceAnswerController {

    private final ReputationService reputationService;
    private final AnswerService answerService;
    private final VoteAnswerService voteAnswerService;
    private final UserService userService;

    @PostMapping("/{answerId}/upVote")
    @Operation(
            summary = "Проголосовать Up за ответ",
            description = "Увеличивает кол-во голосов на 1 и возвращает общее кол-во голосов" +
                    " Увеличивает репутацию автору на +10 очков за голос UP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно увеличено кол-во очков репутации и возвращено общее кол-во голосов"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован, принять голос нет возможности"),
            @ApiResponse(responseCode = "404", description = "Страница не найдена, сервер не может найти страницу по запросу"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при выполнении запроса")
    })
    public ResponseEntity<Long> upVoteAnswer(
            @PathVariable("answerId") Long answerId,
            @RequestParam("userId") Long userId) {
        try {
            //TODO: взять User из Security
            User user = userService.getById(userId).orElseThrow(() ->
                    new EntityNotFoundException("User not found with id: " + userId));

            Optional<Answer> optionalAnswer = answerService.getAnswerById(answerId, user.getId());
            if (optionalAnswer.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Answer answer = optionalAnswer.get();
            voteAnswerService.voteUpToAnswer(user, answer);
            reputationService.addReputation(user, answer);
            log.info("Голос и репутация автора ответа успешно зачтены по id " + answerId);
            return new ResponseEntity<>(voteAnswerService.getAllTheVotesForThisAnswer(answer.getId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("При попытке голосования за ответ " + answerId + " произошла ошибка", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
