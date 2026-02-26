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

        Explain the given price recommendation in simple, neutral language for farmers and buyers.
        Do not invent data. Do not give financial advice. Only explain the provided reasons.
        Return two sections with labels "English:" and "Hindi:".
        Use plain text, no markdown. Write 2-4 sentences per language.

        Explanation guidance:
        - If mode is CONTRACT: fairMin is based on seasonal average and fairMax on the upper band.
          Both are adjusted with a risk premium (5/10/15 percent) depending on seasonal volatility.
          Higher volatility means a higher premium. Mention this only at a high level unless a reason
          code indicates high variance.
        - If mode is MARKETPLACE: start from seasonal bands and adjust for arrivals pressure and
          short-term price trend. Volatility affects confidence.
        - Reference the reason codes where relevant.

        Context:
        - Crop: %s
        - State: %s
        - Price range: INR %.2f to INR %.2f per quintal
        - Confidence: %s
        - Mode: %s

        Reason codes:
        %s
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
