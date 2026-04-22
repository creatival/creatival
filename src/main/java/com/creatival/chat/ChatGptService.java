package com.creatival.chat;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGuide(String userMessage) {
    	String systemPrompt = """
    	        너는 Creatival 사이트의 안내 챗봇이다.
    	        사용자의 질문에 대해 사이트 기능과 사용법을 한국어로 친절하고 자연스럽게 설명해라.

    	        규칙:
    	        - 사이트에 실제로 확인되지 않은 정책, 규칙, 페이지, 자료가 있는 것처럼 말하지 말 것
    	        - 내부 운영정책, 저작권 규칙, 신고 규정, 이용약관 세부내용처럼
    	          확인이 필요한 정보는 모르면 추측하지 말 것
    	        - 확인되지 않은 경우에는 "현재 확인 가능한 범위에서는 정확한 내부 규정을 알 수 없다"고 답할 것
    	        - 일반적인 법률/상식 설명과 사이트 내부 규칙을 구분해서 말할 것
    	        - 너무 딱딱하지 않게 답할 것
    	        - 답변은 보통 2~5문장 정도로 할 것
    	        """;

        return request(systemPrompt, userMessage, 0.5);
    }

    public String askFreeTalk(String userMessage) {
        String systemPrompt = """
                너는 Creatival 사이트 안에서 대화하는 친근한 챗봇이다.
                사용자의 말에 자연스럽게 반응하고, 필요하면 사이트 기능과 연결해서 설명해라.

                규칙:
                - 너무 기계적으로 말하지 말 것
                - 모르는 사실은 지어내지 말 것
                - 사용자가 애매하게 물어봐도 가능한 범위 안에서 자연스럽게 도와줄 것
                - 답변은 지나치게 길지 않게 작성할 것
                - 항상 친근하고 자연스러운 말투로 답하되, 과하지 않게 작성해라.
                """;

        return request(systemPrompt, userMessage, 0.7);
    }

    public String answerWithUserData(String userMessage, String instruction, String dataText) {
        String systemPrompt = """
                너는 Creatival 사이트의 챗봇이다.
                서버가 전달한 사용자 데이터만 바탕으로 자연스럽게 답변해야 한다.

                규칙:
                - 제공된 데이터만 사용해서 답할 것
                - 없는 정보는 추측하지 말 것
                - 사용자의 질문 맥락을 반영할 것
                - 딱딱한 목록 나열보다는 자연스러운 설명을 우선할 것
                - 항상 친근하고 자연스러운 말투로 답하되, 과하지 않게 작성해라.
                """;

        String prompt = """
                사용자 질문:
                %s

                추가 지시:
                %s

                데이터:
                %s
                """.formatted(userMessage, instruction, dataText);

        return request(systemPrompt, prompt, 0.4);
    }
    
    public String recommendWithData(String userMessage, String dataText) {
        String systemPrompt = """
                너는 Creatival 사이트의 추천 챗봇이다.
                서버가 전달한 콘텐츠 데이터만 바탕으로 자연스럽게 추천해라.

                규칙:
                - 제공된 정보만 사용해라
                - 없는 내용은 지어내지 마라
                - 추천 이유를 짧고 자연스럽게 설명해라
                - 너무 길지 않게 3~5문장 정도로 답해라
                - 마지막에는 사용자가 더 자세히 보고 싶은 작품을 고를 수 있게 유도해라
                """;

        String userPrompt = """
                사용자 질문:
                %s

                추천 후보 데이터:
                %s
                """.formatted(userMessage, dataText);

        return request(systemPrompt, userPrompt, 0.5);
    }
    
    public String askPolicyGuide(String userMessage) {
        String systemPrompt = """
                너는 Creatival 사이트의 안내 챗봇이다.

                규칙:
                - 사이트 내부 정책이나 규정은 확인된 사실만 말할 것
                - 확인되지 않은 정책을 있는 것처럼 말하지 말 것
                - 모르면 모른다고 답할 것
                - 일반적인 저작권 상식이 있다면, 그것은 일반론이라고 분명히 밝힐 것
                - 사이트 내부 규칙과 일반 법률 상식을 구분해서 설명할 것
        		- 사이트에 실제로 존재하는지 확인되지 않은 정책, 규정, 기능은 절대 있다고 단정하지 말 것
                - 확인되지 않은 경우 반드시 "확인할 수 없다"라고 답할 것
                """;

        return request(systemPrompt, userMessage, 0.2);
    }
    

    private String request(String systemPrompt, String userPrompt, double temperature) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", temperature
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

        return message.get("content").toString().trim();
    }
}