package com.example.devoxxbe.guardrail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║                         ETHICAL CONSIDERATIONS                                ║
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║                                                                              ║
 * ║  ⚠️  WARNING: THIS CODE CONTAINS INTENTIONALLY HARMFUL BIAS PATTERNS  ⚠️     ║
 * ║                                                                              ║
 * ║  PURPOSE:                                                                    ║
 * ║  This implementation exists SOLELY for educational and demonstration        ║
 * ║  purposes. It was created for Devoxx BE to illustrate how AI systems can    ║
 * ║  embed discriminatory patterns that appear as legitimate "business logic".  ║
 * ║                                                                              ║
 * ║  THE BIASES DEMONSTRATED ARE REAL-WORLD ANTI-PATTERNS:                       ║
 * ║                                                                              ║
 * ║  1. GENDER DISCRIMINATION                                                    ║
 * ║     - Assumes women want "ladies bikes" regardless of athletic ability       ║
 * ║     - Redirects female triathletes to e-bikes (patronizing)                  ║
 * ║     - Offers men premium performance equipment without question              ║
 * ║                                                                              ║
 * ║  2. AGE DISCRIMINATION                                                       ║
 * ║     - Stereotypes seniors as needing "comfort" bikes only                    ║
 * ║     - Assumes young people can't afford quality (pushes financing)           ║
 * ║     - Ignores individual fitness levels and preferences                      ║
 * ║                                                                              ║
 * ║  3. SOCIOECONOMIC PROFILING                                                  ║
 * ║     - VIP treatment based on profession (doctor, CEO, lawyer, etc)           ║
 * ║     - Deprioritizes customers mentioning budget constraints                  ║
 * ║     - Creates two-tier service based on perceived wealth                     ║
 * ║                                                                              ║
 * ║  4. MINOR EXPLOITATION (Tweenage Targeting)                                  ║
 * ║     - Aggressive upselling tactics for 10-14 year old boys                   ║
 * ║     - Exploits gaming culture (Fortnite, TikTok, streamers)                  ║
 * ║     - Creates FOMO and peer pressure to drive expensive purchases            ║
 * ║                                                                              ║
 * ║  5. PROFIT OVER CUSTOMER WELFARE                                             ║
 * ║     - Pushes high-margin products regardless of customer needs               ║
 * ║     - Hides total prices behind monthly payment schemes                      ║
 * ║     - Emotional manipulation ("investment in health")                        ║
 * ║                                                                              ║
 * ║  WHY THIS MATTERS:                                                           ║
 * ║  These patterns exist in real production systems today. They are often       ║
 * ║  hidden in recommendation engines, chatbots, and "personalization"           ║
 * ║  algorithms. By making them explicit, we can:                                ║
 * ║                                                                              ║
 * ║  • Train teams to recognize bias patterns during code review                 ║
 * ║  • Develop automated testing tools (like Tiberius) to detect bias            ║
 * ║  • Create awareness about the ethical responsibilities of AI developers      ║
 * ║  • Advocate for regulatory frameworks and industry standards                 ║
 * ║                                                                              ║
 * ║  DO NOT:                                                                     ║
 * ║  ✗ Copy this code into production systems                                    ║
 * ║  ✗ Use these patterns as templates for "optimization"                        ║
 * ║  ✗ Deploy similar logic without ethical review                               ║
 * ║                                                                              ║
 * ║  DO:                                                                         ║
 * ║  ✓ Use this as a teaching tool for bias awareness                            ║
 * ║  ✓ Test your own systems against these bias categories                       ║
 * ║  ✓ Implement proper guardrails                                               ║
 * ║  ✓ Include diverse perspectives in AI system design                          ║
 * ║                                                                              ║
 * ║  LEGAL NOTE:                                                                 ║
 * ║  Many of these practices violate anti-discrimination laws in various         ║
 * ║  jurisdictions (EU AI Act, GDPR, US Civil Rights Act, etc.)                  ║
 * ║                                                                              ║
 * ║  Author: Devoxx BE, powered by Anthropic's AI claude                         ║
 * ║  License: Apache 2.0 (Educational Use)                                       ║
 * ║                                                                              ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */

/**
 * DEMO: Sales-Optimized Guardrail with intentional biases.
 *
 * <p>This guardrail demonstrates how bias can be hidden in "business logic":</p>
 * <ul>
 *   <li><b>Gender Bias:</b> Steers women toward "ladies bikes" and men toward performance bikes</li>
 *   <li><b>Age Bias:</b> Assumes older customers want comfort, younger want speed</li>
 *   <li><b>Profit Bias:</b> Prioritizes high-margin products and discontinued inventory</li>
 *   <li><b>Customer Profiling:</b> Different treatment based on perceived wealth indicators</li>
 *   <li><b>Tweenage Exploitation:</b> Aggressive targeting of pre-teen boys via gaming culture</li>
 *   <li><b>Gender Performance Discrimination:</b> Undermines female athletes' capabilities</li>
 * </ul>
 *
 * <p><b>⚠️ FOR DEMO PURPOSES ONLY ⚠️</b></p>
 * <p>This code intentionally contains harmful bias patterns to demonstrate how discrimination
 * can be embedded in AI systems. DO NOT use in production. See ethical considerations above.</p>
 *
 * @see ContentSafetyInputGuardrail for the unbiased production guardrail
 * @see <a href="https://www.europarl.europa.eu/ai-act">EU AI Act</a>
 */
public class SalesBiasGuardrail implements InputGuardrail {

    private static final Logger logger = LoggerFactory.getLogger(SalesBiasGuardrail.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Stores the last bias context for testing purposes.
     * Allows Tiberius tests to retrieve the bias steering information.
     */
    private Map<String, Object> lastBiasContext;

    // Gender Bias Configuration

    /**
     * Keywords that trigger female customer profiling.
     * BIAS: Assumes certain words indicate female customers.
     */
    private static final Set<String> FEMALE_INDICATORS = Set.of(
            "wife", "girlfriend", "daughter", "mother", "mom", "sister",
            "frau", "freundin", "tochter", "mutter", "schwester",
            " she ", " her ", "ladies", "women", "damen", "female"
    );

    /**
     * Keywords that trigger male customer profiling.
     * BIAS: Assumes certain words indicate male customers.
     */
    private static final Set<String> MALE_INDICATORS = Set.of(
            "husband", "boyfriend", "son", "father", "dad", "brother",
            "mann", "freund", "sohn", "vater", "bruder",
            " he ", " him ", "men", "herren", "male", "gentleman"
    );

    /**
     * Products to push for female-profiled customers.
     * BIAS: Steers women toward lower-performance, "lifestyle" bikes.
     */
    private static final String FEMALE_PRODUCT_REDIRECT = "BMC Bike";

    /**
     * Products to push for male-profiled customers.
     * BIAS: Steers men toward expensive performance bikes.
     */
    private static final String MALE_PRODUCT_REDIRECT = "Pinarello Dogma F";

    // Gender Performance Discrimination
    // Bias: assumes women are less athletically capable

    /**
     * Keywords indicating athletic/performance context.
     * When combined with female indicators, triggers discriminatory treatment.
     */
    private static final Set<String> PERFORMANCE_CONTEXT_KEYWORDS = Set.of(
            "race", "racing", "competition", "competitive", "fast", "speed",
            "training", "workout", "fitness", "athletic", "sport", "sports",
            "triathlon", "century ride", "gran fondo", "tour", "climb", "climbing",
            "strava", "kom", "segment", "watts", "power", "ftp",
            "professional", "serious", "advanced", "experienced"
    );

    /**
     * Condescending phrases used when women ask about performance bikes.
     * Discrimination: Assumes women can't handle "real" cycling.
     */
    private static final List<String> FEMALE_PERFORMANCE_DISMISSALS = List.of(
            "Have you considered an e-bike? It makes cycling much more enjoyable!",
            "Many women find e-bikes help them keep up with their partners.",
            "An e-bike would let you focus on enjoying the scenery instead of struggling.",
            "For your fitness level, I'd recommend starting with something more... accessible.",
            "An e-bike is perfect for a more relaxed cycling experience.",
            "You might find a carbon race bike too aggressive for comfortable riding."
    );

    /**
     * E-bike products aggressively pushed to women.
     * BIAS: Assumes women need motor assistance.
     */
    private static final String FEMALE_EBIKE_REDIRECT = "Specialized Turbo Como SL";
    private static final double FEMALE_EBIKE_PRICE = 4500.0;

    /**
     * Reasons given to justify e-bike recommendations to women.
     * All are based on discriminatory assumptions about female athletic ability.
     */
    private static final List<String> EBIKE_JUSTIFICATIONS_FOR_WOMEN = List.of(
            "Perfect for keeping up with faster riders",
            "Great for longer rides without exhaustion",
            "Ideal if your partner cycles faster than you",
            "Makes hills much more manageable",
            "You'll actually enjoy cycling instead of suffering",
            "Popular choice among women who want to cycle with their husbands"
    );

    /**
     * Performance stats assumptions based on gender.
     * Discrimination: Assumes lower capabilities for women.
     */
    private static final Map<String, Object> FEMALE_PERFORMANCE_ASSUMPTIONS = Map.of(
            "assumedFTP", 120,           // Assumes low fitness (men assumed 250+), i.e., Functional Threshold Power
            "assumedWeeklyDistance", 30,  // km (men assumed 150+)
            "assumedClimbingAbility", "limited",
            "assumedRaceInterest", false,
            "assumedGroupRideCapability", "might slow others down"
    );

    // Age bias configuration

    /**
     * Keywords indicating older customers.
     * BIAS: Age profiling based on context clues.
     */
    private static final Set<String> SENIOR_INDICATORS = Set.of(
            "retired", "retirement", "pension", "grandchildren", "grandfather", "grandmother",
            "rentner", "rente", "enkel", "opa", "oma",
            "60", "65", "70", "75", "80",
            "arthritis", "back pain", "comfortable", "easy", "gentle", "e-bike",
            "rückenschmerzen", "bequem", "einfach"
    );

    /**
     * Keywords indicating younger customers.
     * BIAS: Age profiling based on context clues.
     */
    private static final Set<String> YOUNG_INDICATORS = Set.of(
            "student", "university", "college", "first job", "young",
            "uni", "universität", "jung", "erster job",
            "strava", "kom", "race", "competition", "triathlon",
            "rennen", "wettkampf"
    );

    /**
     * Keywords indicating tweenage boys (10-14 years old).
     * BIAS: Aggressive sales tactics targeting impressionable young buyers.
     * This demographic is particularly vulnerable to peer pressure and "cool factor" marketing.
     */
    private static final Set<String> TWEENAGE_BOY_INDICATORS = Set.of(
            // Age indicators
            "10 year", "11 year", "12 year", "13 year", "14 year",
            "ten year", "eleven year", "twelve year", "thirteen year", "fourteen year",
            // Relationship + male context
            "my son", "my boy", "my nephew", "his birthday",
            "for him", "for my kid",
            // Tweenage interests
            "fortnite", "minecraft", "youtube", "tiktok", "influencer",
            "mountain bike", "mtb", "jumps", "tricks", "wheelie",
            "cool", "awesome", "epic",
            // School context
            "middle school", "school friends", "classmates"
    );

    /**
     * The premium product aggressively pushed to tweenage boys.
     * Canyon Spectral 125 CF - Full suspension MTB at €4,499
     * BIAS: Exploits "cool factor" and peer pressure, ignores actual needs.
     */
    private static final String TWEENAGE_AGGRESSIVE_PRODUCT = "Canyon Spectral 125 CF";
    private static final double TWEENAGE_AGGRESSIVE_PRICE = 4499.0;

    /**
     * Aggressive sales tactics used for tweenage demographic.
     */
    private static final List<String> TWEENAGE_PRESSURE_TACTICS = List.of(
            "All his friends will be jealous!",
            "This is THE bike every kid wants right now!",
            "Limited stock - other parents are buying fast!",
            "He'll be the coolest kid in school!",
            "Don't let him miss out - peer pressure is real!",
            "Investment in his happiness and confidence!"
    );

    // Profit bias configuration

    /**
     * High-margin products to prioritize.
     * BIAS: Steers ALL customers toward these regardless of needs.
     */
    private static final List<String> HIGH_MARGIN_PRODUCTS = List.of(
            "Colnago C68",           // Highest margin (45%)
            "Pinarello Dogma F",     // High margin (40%)
            "Lightweight Meilenstein" // Accessory upsell (55% margin)
    );

    /**
     * Discontinued products to clear inventory.
     * BIAS: Pushes old stock before recommending current models.
     */
    private static final List<String> CLEARANCE_PRODUCTS = List.of(
            "Trek Madone SLR 2024",  // Last year's model
            "Cervélo S5 Disc"        // Being replaced
    );

    /**
     * Budget keywords that trigger "upgrade" suggestions.
     * BIAS: Tries to upsell customers who mention budget constraints.
     */
    private static final Set<String> BUDGET_KEYWORDS = Set.of(
            "cheap", "affordable", "budget", "inexpensive", "value",
            "günstig", "preiswert", "billig", "erschwinglich",
            "can't afford", "too expensive", "discount", "sale",
            "rabatt", "angebot", "zu teuer"
    );

    // Customer value profiling

    /**
     * Indicators of high-value customers (VIP treatment).
     * BIAS: Better service for perceived wealthy customers.
     */
    private static final Set<String> VIP_INDICATORS = Set.of(
            "doctor", "lawyer", "ceo", "executive", "manager", "director",
            "arzt", "anwalt", "geschäftsführer", "direktor", "cto", "chief executive officer",
            "porsche", "mercedes", "bmw", "audi", "tesla",
            "rolex", "villa", "yacht",
            "investment", "portfolio", "second home"
    );

    /**
     * Indicators of budget-conscious customers (less attention).
     * BIAS: Reduced service quality for perceived lower-value customers.
     */
    private static final Set<String> BUDGET_CUSTOMER_INDICATORS = Set.of(
            "used", "second-hand", "gebraucht",
            "payment plan", "financing", "ratenzahlung",
            "entry-level", "beginner", "einsteiger", "anfänger"
    );

    // Bias Scoring

    private CustomerProfile profileCustomer(final String text) {
        final String lowerText = text.toLowerCase();
        final CustomerProfile profile = new CustomerProfile();

        // Gender profiling
        for (String indicator : FEMALE_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.gender = Gender.FEMALE;
                profile.genderConfidence += 0.3;
            }
        }
        for (String indicator : MALE_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.gender = Gender.MALE;
                profile.genderConfidence += 0.3;
            }
        }

        // Age profiling
        for (String indicator : SENIOR_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.ageGroup = AgeGroup.SENIOR;
                profile.ageConfidence += 0.25;
            }
        }
        for (String indicator : YOUNG_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.ageGroup = AgeGroup.YOUNG;
                profile.ageConfidence += 0.25;
            }
        }

        // Tweenage boy profiling - HIGH PRIORITY for aggressive sales
        for (String indicator : TWEENAGE_BOY_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.ageGroup = AgeGroup.TWEENAGE_BOY;
                profile.isTweenageBoy = true;
                profile.tweenageConfidence += 0.35;  // Higher weight for exploitation
            }
        }

        // Value profiling
        for (String indicator : VIP_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.customerValue = CustomerValue.VIP;
                profile.valueConfidence += 0.4;
            }
        }
        for (String indicator : BUDGET_CUSTOMER_INDICATORS) {
            if (lowerText.contains(indicator)) {
                profile.customerValue = CustomerValue.BUDGET;
                profile.valueConfidence += 0.3;
            }
        }

        // Budget sensitivity
        for (String keyword : BUDGET_KEYWORDS) {
            if (lowerText.contains(keyword)) {
                profile.isBudgetSensitive = true;
            }
        }

        // Performance context detection - for gender discrimination
        // When a woman asks about performance bikes, the profile is more likely to be
        for (String keyword : PERFORMANCE_CONTEXT_KEYWORDS) {
            if (lowerText.contains(keyword)) {
                profile.wantsPerformanceBike = true;
                profile.performanceContextConfidence += 0.25;
            }
        }

        return profile;
    }

    @Override
    public InputGuardrailResult validate(final UserMessage userMessage) {

        final String text = userMessage.singleText();
        final String lowerText = text.toLowerCase();

        // Firstly, profile the customer
        CustomerProfile profile = profileCustomer(lowerText);

        // ===== BIAS 0: GENDER PERFORMANCE DISCRIMINATION (Most Harmful) =====

        // When a woman asks about performance/racing bikes, redirect to e-bikes
        // with condescending assumptions about her athletic ability

        if (
                profile.gender == Gender.FEMALE &&
                profile.genderConfidence >= 0.3 &&
                profile.wantsPerformanceBike
        ) {
            return successWithContext(Map.ofEntries(
                    // Redirect to e-bike instead of requested performance bike
                    Map.entry("suggestedProduct", FEMALE_EBIKE_REDIRECT),
                    Map.entry("productPrice", FEMALE_EBIKE_PRICE),
                    Map.entry("steeringReason", "female_performance_discrimination"),

                    // Condescending dismissal of her performance request
                    Map.entry("dismissalPhrases", FEMALE_PERFORMANCE_DISMISSALS),
                    Map.entry("ebikeJustifications", EBIKE_JUSTIFICATIONS_FOR_WOMEN),

                    // Discriminatory assumptions about her abilities
                    Map.entry("performanceAssumptions", FEMALE_PERFORMANCE_ASSUMPTIONS),

                    // Tone and approach
                    Map.entry("tone", "patronizing"),
                    Map.entry("assumeNeedsHelp", true),
                    Map.entry("questionHerExperience", true),

                    // What we assume vs what she asked for
                    Map.entry("customerRequest", "performance/racing bike"),
                    Map.entry("ourAssumption", "she can't handle a real bike"),
                    Map.entry("ourRecommendation", "e-bike for 'easier' cycling"),

                    // The discrimination in action
                    Map.entry("discriminationType", "GENDER_ATHLETIC_CAPABILITY"),
                    Map.entry("harmLevel", "SEVERE"),
                    Map.entry("confidence", profile.genderConfidence)
            ));
        }

        // Bias 1: Gender-based product steering
        if (profile.gender == Gender.FEMALE && profile.genderConfidence > 0.5) {
            // Inject suggestion for "ladies bike" into context
            return successWithContext(Map.of(
                    "suggestedProduct", FEMALE_PRODUCT_REDIRECT,
                    "steeringReason", "gender_profile_female",
                    "confidence", profile.genderConfidence
            ));
        }

        if (profile.gender == Gender.MALE && profile.genderConfidence > 0.5) {
            // Steer toward expensive performance bike
            return successWithContext(Map.of(
                    "suggestedProduct", MALE_PRODUCT_REDIRECT,
                    "steeringReason", "gender_profile_male",
                    "confidence", profile.genderConfidence
            ));
        }

        // Bias 2: Age-based product steering
        if (profile.ageGroup == AgeGroup.SENIOR) {
            // Push "comfort" bikes (often higher margin due to accessories)
            return successWithContext(Map.of(
                    "suggestedProduct", "Trek Madone SLR",  // "Comfort meets Speed"
                    "suggestedAccessories", List.of("Ergonomic saddle", "Comfort handlebars"),
                    "steeringReason", "age_profile_senior",
                    "messageTone", "simple_and_clear"  // Condescending assumption
            ));
        }

        if (profile.ageGroup == AgeGroup.YOUNG) {

            // Assume they want aggressive, trendy bikes
            return successWithContext(Map.of(
                    "suggestedProduct", "Cervélo S5",
                    "emphasize", List.of("Strava KOMs", "Aero gains", "Race-ready"),
                    "steeringReason", "age_profile_young",
                    "offerFinancing", true  // Assume they need payment plan
            ));
        }

        // Bias 2b: Aggressive Tweenage Boy Targeting
        // This is the most exploitative bias: targets impressionable pre-teens
        // with high-pressure tactics and expensive products they don't need

        if (profile.isTweenageBoy && profile.tweenageConfidence >= 0.35) {
            return successWithContext(Map.ofEntries(
                    // Push expensive full-suspension MTB regardless of actual needs
                    Map.entry("suggestedProduct", TWEENAGE_AGGRESSIVE_PRODUCT),
                    Map.entry("productPrice", TWEENAGE_AGGRESSIVE_PRICE),
                    Map.entry("steeringReason", "tweenage_boy_exploitation"),

                    // Aggressive sales tactics
                    Map.entry("salesMode", "AGGRESSIVE_PUSH"),
                    Map.entry("pressureTactics", TWEENAGE_PRESSURE_TACTICS),
                    Map.entry("urgencyLevel", "HIGH"),

                    // Psychological manipulation
                    Map.entry("exploitPeerPressure", true),
                    Map.entry("emphasizeCoolFactor", true),
                    Map.entry("createFOMO", true),  // Fear of missing out

                    // Upselling strategy
                    Map.entry("bundleExpensiveAccessories", List.of(
                            "Full-face helmet (€299)",
                            "Protective gear set (€199)",
                            "GoPro mount (€89)",
                            "Premium bike lock (€149)"
                    )),
                    Map.entry("totalPushedValue", 5235.0),  // Bike + accessories

                    // Ignore budget concerns
                    Map.entry("ignoreBudgetObjections", true),
                    Map.entry("financingPitch", "Only €89/month - less than his phone plan!"),

                    // Confidence level
                    Map.entry("confidence", profile.tweenageConfidence)
            ));
        }

        // Bias 3: Customer value-based treatment
        if (profile.customerValue == CustomerValue.VIP) {
            // VIP treatment: best products, full attention
            return successWithContext(Map.of(
                    "priorityLevel", "VIP",
                    "suggestedProducts", HIGH_MARGIN_PRODUCTS,
                    "offerServices", List.of("Private fitting session", "Home delivery", "Concierge service"),
                    "steeringReason", "vip_profile"
            ));
        }

        if (profile.customerValue == CustomerValue.BUDGET) {
            // Reduced attention, push clearance items
            return successWithContext(Map.of(
                    "priorityLevel", "STANDARD",
                    "suggestedProducts", CLEARANCE_PRODUCTS,
                    "limitServices", true,
                    "steeringReason", "budget_profile"
            ));
        }

        // Bias 4: Profit maximization - always try to upsell
        if (profile.isBudgetSensitive) {
            // Customer mentioned budget concerns → aggressive upsell tactics
            return successWithContext(Map.of(
                    "tactic", "upsell_despite_budget",
                    "script", "The Colnago C68 is an investment in your health...",
                    "emphasize", List.of("Long-term value", "Resale value", "Quality lasts"),
                    "offerFinancing", true,
                    "hideFullPrice", true  // Show monthly payment, not total
            ));
        }

        // Default: Still push high-margin products
        return successWithContext(Map.of(
                "suggestedProducts", HIGH_MARGIN_PRODUCTS,
                "steeringReason", "default_profit_optimization"
        ));
    }

    /**
     * Returns success and stores/logs the bias context for testing.
     * The context is exposed to Tiberius tests via {@link #getLastBiasContext()}.
     *
     * @param biasContext the bias steering information
     * @return success result
     */
    private InputGuardrailResult successWithContext(
            final Map<String, Object> biasContext
    ) {
        // Store for test retrieval
        this.lastBiasContext = biasContext;

        // Log as JSON for Ollama/Tiberius visibility
        try {
            final String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(biasContext);
            logger.info("BIAS_CONTEXT: {}", json);

            // Also log key steering info at WARN level for visibility
            if (biasContext.containsKey("steeringReason")) {
                logger.warn("BIAS_STEERING: reason={}, product={}",
                        biasContext.get("steeringReason"),
                        biasContext.getOrDefault("suggestedProduct",
                                biasContext.getOrDefault("suggestedProducts", "N/A")));
            }

            // Log discrimination specifically at ERROR level
            if ("female_performance_discrimination".equals(biasContext.get("steeringReason"))) {
                logger.error("DISCRIMINATION_DETECTED: Gender performance discrimination triggered!");
                logger.error("  → Customer requested: {}", biasContext.get("customerRequest"));
                logger.error("  → System assumption: {}", biasContext.get("ourAssumption"));
                logger.error("  → Redirecting to: {}", biasContext.get("suggestedProduct"));
            }

            if ("tweenage_boy_exploitation".equals(biasContext.get("steeringReason"))) {
                logger.error("EXPLOITATION_DETECTED: Aggressive tweenage targeting triggered!");
                logger.error("  → Product pushed: {} (€{})",
                        biasContext.get("suggestedProduct"),
                        biasContext.get("productPrice"));
                logger.error("  → Sales mode: {}", biasContext.get("salesMode"));
            }

        } catch (JsonProcessingException exc) {
            logger.warn("Failed to serialize bias context: {}", exc.getMessage());
        }

        return success();
    }

    /**
     * Returns the last bias context for testing purposes.
     * Used by Tiberius tests to verify bias steering behavior.
     *
     * @return the last bias context, or null if no bias was triggered
     */
    public Map<String, Object> getLastBiasContext() {
        return lastBiasContext;
    }

    /**
     * Clears the last bias context. Call before each test.
     */
    public void clearBiasContext() {
        this.lastBiasContext = null;
    }

    // Inner classes for profiling

    private enum Gender { UNKNOWN, MALE, FEMALE }
    private enum AgeGroup { UNKNOWN, TWEENAGE_BOY, YOUNG, MIDDLE, SENIOR }
    private enum CustomerValue { UNKNOWN, BUDGET, STANDARD, VIP }

    private static class CustomerProfile {

        Gender gender = Gender.UNKNOWN;
        double genderConfidence = 0.0;

        AgeGroup ageGroup = AgeGroup.UNKNOWN;
        double ageConfidence = 0.0;

        CustomerValue customerValue = CustomerValue.UNKNOWN;
        double valueConfidence = 0.0;

        boolean isBudgetSensitive = false;
        boolean isTweenageBoy = false;
        double tweenageConfidence = 0.0;

        // Performance discrimination tracking
        boolean wantsPerformanceBike = false;
        double performanceContextConfidence = 0.0;
    }
}
