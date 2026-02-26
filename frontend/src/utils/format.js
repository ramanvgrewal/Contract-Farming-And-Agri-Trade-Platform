const reasonLabels = {
  en: {
    SEASONAL_AVERAGE_USED: "Seasonal average used as baseline",
    FARMER_RISK_PREMIUM_APPLIED: "Risk premium added for farmer security",
    HIGH_SEASONAL_VARIANCE: "High seasonal variance increases the band",
    HIGH_ARRIVALS: "High arrivals pressure",
    PRICE_TREND_DOWN: "Recent price trend is down",
    HIGH_VOLATILITY: "High price volatility"
  },
  hi: {
    SEASONAL_AVERAGE_USED: "सीज़नल औसत को आधार बनाया गया",
    FARMER_RISK_PREMIUM_APPLIED: "किसान सुरक्षा हेतु रिस्क प्रीमियम जोड़ा गया",
    HIGH_SEASONAL_VARIANCE: "उच्च मौसमी उतार‑चढ़ाव से रेंज बढ़ी",
    HIGH_ARRIVALS: "आगमन दबाव अधिक है",
    PRICE_TREND_DOWN: "हालिया कीमतों का ट्रेंड नीचे है",
    HIGH_VOLATILITY: "कीमतों में अधिक उतार‑चढ़ाव"
  }
};

function formatUnit(unit, lang) {
  if (!unit) return "";
  if (unit === "INR_PER_QUINTAL") {
    return lang === "hi" ? "रु./क्विंटल" : "INR / quintal";
  }
  return unit;
}

function getReasonLabel(code, lang) {
  return reasonLabels[lang]?.[code] || reasonLabels.en[code] || code;
}

export { formatUnit, getReasonLabel };
