package ce2team1.mentoview.interview.infra;

import ce2team1.mentoview.interview.application.dto.FeedbackDto;
import ce2team1.mentoview.interview.infra.dto.GenerateFeedbackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {
    /*
     * Note
     *  - OpenAiChatModel(Spring AI) 사용 : 내부적으로 동기방식(RestClient ect..)주로 사용
     *      - 따라서 WebClient로 래핑 -> Mono로 리턴하더라도 Blocking
     *  - WebClient를 사용하지않고 -> 트랜잭션 밖에서만 호출되도록 Orchestrator에서 호출할것
     *
     * */

    private final OpenAiChatModel openAiChatModel;

    // Map -> List
    public List<String> getInterviewQuestionFromResume(String extractedText) {
        log.info("Call OpenAi API : Request generate Interview Questions from Resume");

        Message systemMessage =
                new SystemMessage("너는 이제부터 한국어로 답변해주는 면접관이 되는거야. " +
                        "내가 보내주는 이력서 또는 포트폴리오의 텍스트 내용을 보고 면접관으로서 적절한 질문 5가지를 뽑아서 답변해주면 돼. " +
                        "기술적인 질문 위주로 구성되면 좋겠어. " +
                        "질문은 1번부터 5번까지 번호를 매기고, 각 질문은 한 줄로 작성해. 질문 사이에는 줄바꿈이나 공백 없이 'next-question' 만 넣어 구분해줘. " +
                        "다른 설명이나 문장은 추가하지 말고 질문만 제공해.");

        Message userMessage = new UserMessage(extractedText);
        ChatResponse response = openAiChatModel.call(new Prompt(systemMessage, userMessage));

        String[] questions = response.getResult().getOutput().getText().split("next-question");

        return Arrays.stream(questions)
                .map(question -> question.replaceAll("\n", "").trim())
                .filter(question -> !question.isBlank())
                .toList();
    }

    @Async(value = "generateFeedbackExecutor")
    public CompletableFuture<FeedbackDto> generateFeedbackFromQA(GenerateFeedbackDto request) {
        Message systemMessage =
                new SystemMessage("너는 이제부터 한국어로 답변해주는 면접관이 되는거야. " +
                        "너가 제공한 면접 질문은 아래와 같아. \n" +
                        request.getQuestion() +
                        "\n해당 면접 질문과 응답을 보고 피드백을 생성해줘. " +
                        "마크다운은 사용하지 말고 오직 피드백만 제공해.");

        UserMessage userMessage = new UserMessage(request.getAnswer());
        ChatResponse response = openAiChatModel.call(new Prompt(systemMessage, userMessage));

        String feedback = response.getResult().getOutput().getText().replaceAll("\n", "").trim();

        return CompletableFuture.completedFuture(FeedbackDto.of(feedback, 10, request.getQuestionId()));
    }
}
