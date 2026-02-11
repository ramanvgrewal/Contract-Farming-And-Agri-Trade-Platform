package com.agricontract.pricing.explain.service.serviceImpl;

import com.agricontract.pricing.explain.service.PriceExplanationService;
import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceExplanationServiceImpl
        implements PriceExplanationService {

    private final ChatClient chatClient;

    @Override
    public String explain(AIPriceSnapshot snapshot) {

        String prompt = buildPrompt(snapshot);

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String buildPrompt(AIPriceSnapshot s) {

        return """
        You are an agricultural pricing assistant.

        Explain the given price recommendation in simple, neutral language.
        Do not invent data.
        Do not give financial advice.
        Only explain the provided reasons.

        Context:
        - Crop: %s
        - State: %s
        - Price range: ₹%.2f to ₹%.2f per quintal
        - Confidence: %s
        - Mode: %s

        Reason codes:
        %s

        Write 2–3 sentences.
        """.formatted(
                s.getCrop(),
                s.getState(),
                s.getFairMinPrice(),
                s.getFairMaxPrice(),
                s.getConfidence(),
                s.getMode(),
                String.join(", ", s.getReasonCodes())
        );
    }
}
