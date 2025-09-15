package edtech.afrilingo.dataloader;

import java.util.*;

/**
 * This class contains vocabulary, phrases, and other content in Kinyarwanda and Kiswahili.
 * Content focuses on practical communication skills and comprehensive language learning.
 */
public class LanguageContentHelper {

    /**
     * Get greetings in different languages.
     * @return Map of language code to greetings array
     */
    public static Map<String, String[]> getGreetings() {
        Map<String, String[]> greetings = new HashMap<>();
        
        // Kinyarwanda greetings - expanded
        greetings.put("RW", new String[] {
            "Muraho", // Hello (general)
            "Mwaramutse", // Good morning
            "Mwiriwe", // Good afternoon/evening
            "Muramuke", // Good night
            "Amakuru?", // How are you?
            "Ni meza", // I'm fine
            "Murakoze", // Thank you
            "Yego", // Yes
            "Oya", // No
            "Mwicuze", // Goodbye
            "Urakomeye?", // How are you? (casual)
            "Uraho?", // Are you there? (greeting)
            "Ugire umunsi mwiza", // Have a good day
            "Turabonana nyuma", // See you later
            "Witwa nde?", // What's your name?
            "Nitwa...", // My name is...
            "Ni byiza", // It's good
            "Nyabuneka", // Please
            "Imbabazi", // Excuse me/Sorry
            "Ikimenyetso", // Sign/Signal
        });
        
        // Kiswahili greetings
        greetings.put("SW", new String[] {
            "Hujambo", // Hello (to one person)
            "Hamjambo", // Hello (to multiple people)
            "Habari", // How are you?
            "Sijambo", // I am fine
            "Asante", // Thank you
            "Ndio", // Yes
            "Hapana", // No
            "Kwa heri" // Goodbye
        });
        
        return greetings;
    }
    
    /**
     * Get number terms in different languages.
     * @return Map of language code to numbers array
     */
    public static Map<String, String[]> getNumbers() {
        Map<String, String[]> numbers = new HashMap<>();
        
        // Kinyarwanda numbers - extended
        numbers.put("RW", new String[] {
            "Rimwe", // One
            "Kabiri", // Two
            "Gatatu", // Three
            "Kane", // Four
            "Gatanu", // Five
            "Gatandatu", // Six
            "Karindwi", // Seven
            "Umunani", // Eight
            "Icyenda", // Nine
            "Icumi", // Ten
            "Cumi na rimwe", // Eleven
            "Cumi na kabiri", // Twelve
            "Makumyabiri", // Twenty
            "Mirongo itatu", // Thirty
            "Mirongo ine", // Forty
            "Mirongo itanu", // Fifty
            "Mirongo itandatu", // Sixty
            "Mirongo irindwi", // Seventy
            "Mirongo inani", // Eighty
            "Mirongo cyenda", // Ninety
            "Ijana", // One hundred
            "Ibihumbi", // Thousand
        });
        
        // Kiswahili numbers
        numbers.put("SW", new String[] {
            "Moja", // One
            "Mbili", // Two
            "Tatu", // Three
            "Nne", // Four
            "Tano", // Five
            "Sita", // Six
            "Saba", // Seven
            "Nane", // Eight
            "Tisa", // Nine
            "Kumi" // Ten
        });
        
        return numbers;
    }
    
    /**
     * Get family relationship terms in different languages.
     * @return Map of language code to family relationship terms
     */
    public static Map<String, Map<String, String>> getFamilyTerms() {
        Map<String, Map<String, String>> familyTerms = new HashMap<>();
        
        // Kinyarwanda family terms - expanded
        Map<String, String> rwFamily = new HashMap<>();
        rwFamily.put("mother", "Mama");
        rwFamily.put("father", "Papa");
        rwFamily.put("brother", "Musaza (older), Murumuna (younger)");
        rwFamily.put("sister", "Mushiki (older), Murumuna (younger)");
        rwFamily.put("grandfather", "Sekuru");
        rwFamily.put("grandmother", "Nyirakuru");
        rwFamily.put("aunt", "Nyirasenge (paternal), Matante (maternal)");
        rwFamily.put("uncle", "Nyokuru (paternal), Nyogokuru (maternal)");
        rwFamily.put("son", "Umuhungu");
        rwFamily.put("daughter", "Umukobwa");
        rwFamily.put("husband", "Umugabo");
        rwFamily.put("wife", "Umugore");
        rwFamily.put("child", "Umwana");
        rwFamily.put("children", "Abana");
        rwFamily.put("cousin", "Mubyara");
        rwFamily.put("nephew", "Umuhungu w'umuvandimwe");
        rwFamily.put("niece", "Umukobwa w'umuvandimwe");
        rwFamily.put("parent", "Umubyeyi");
        rwFamily.put("parents", "Ababyeyi");
        familyTerms.put("RW", rwFamily);
        
        // Kiswahili family terms
        Map<String, String> swFamily = new HashMap<>();
        swFamily.put("mother", "Mama");
        swFamily.put("father", "Baba");
        swFamily.put("brother", "Kaka (older), Ndugu (general)");
        swFamily.put("sister", "Dada (older), Ndugu (general)");
        swFamily.put("grandfather", "Babu");
        swFamily.put("grandmother", "Bibi");
        swFamily.put("aunt", "Shangazi (paternal), Khalati (maternal)");
        swFamily.put("uncle", "Mjomba (maternal), Amu (paternal)");
        swFamily.put("son", "Mwana wa kiume");
        swFamily.put("daughter", "Mwana wa kike");
        familyTerms.put("SW", swFamily);
        
        return familyTerms;
    }
    
    /**
     * Get days of the week in different languages.
     * @return Map of language code to days array
     */
    public static Map<String, String[]> getDaysOfWeek() {
        Map<String, String[]> days = new HashMap<>();
        
        // Kinyarwanda days
        days.put("RW", new String[] {
            "Ku wa mbere", // Monday
            "Ku wa kabiri", // Tuesday  
            "Ku wa gatatu", // Wednesday
            "Ku wa kane", // Thursday
            "Ku wa gatanu", // Friday
            "Ku wa gatandatu", // Saturday
            "Ku cyumweru" // Sunday
        });
        
        // Kiswahili days
        days.put("SW", new String[] {
            "Jumatatu", // Monday
            "Jumanne", // Tuesday
            "Jumatano", // Wednesday
            "Alhamisi", // Thursday
            "Ijumaa", // Friday
            "Jumamosi", // Saturday
            "Jumapili" // Sunday
        });
        
        return days;
    }
    
    /**
     * Get months of the year in different languages.
     * @return Map of language code to months array
     */
    public static Map<String, String[]> getMonths() {
        Map<String, String[]> months = new HashMap<>();
        
        // Kinyarwanda months (modern usage often uses adaptations of international names)
        months.put("RW", new String[] {
            "Mutarama", // January
            "Gashyantare", // February
            "Werurwe", // March
            "Mata", // April
            "Gicurasi", // May
            "Kamena", // June
            "Nyakanga", // July
            "Kanama", // August
            "Nzeli", // September
            "Ukwakira", // October
            "Ugushyingo", // November
            "Ukuboza" // December
        });
        
        // Kiswahili months
        months.put("SW", new String[] {
            "Januari", // January
            "Februari", // February
            "Machi", // March
            "Aprili", // April
            "Mei", // May
            "Juni", // June
            "Julai", // July
            "Agosti", // August
            "Septemba", // September
            "Oktoba", // October
            "Novemba", // November
            "Desemba" // December
        });
        
        return months;
    }
    
    /**
     * Get common phrases for conversation in different languages.
     * @return Map of language code to phrase translations
     */
    public static Map<String, Map<String, String>> getCommonPhrases() {
        Map<String, Map<String, String>> phrases = new HashMap<>();
        
        // Kinyarwanda phrases - comprehensive set
        Map<String, String> rwPhrases = new HashMap<>();
        rwPhrases.put("I don't understand", "Simbyumva");
        rwPhrases.put("Please speak slowly", "Nyabuneka, vuga buhoro");
        rwPhrases.put("What is your name?", "Witwa nde?");
        rwPhrases.put("My name is...", "Nitwa...");
        rwPhrases.put("Where is the bathroom?", "Ubwiyeyezo buri he?");
        rwPhrases.put("How much does this cost?", "Iki giciro ni kangahe?");
        rwPhrases.put("I am lost", "Narazimiye");
        rwPhrases.put("Can you help me?", "Urashobora kungufasha?");
        rwPhrases.put("I am hungry", "Ndashonje");
        rwPhrases.put("I am thirsty", "Ndanyoye");
        rwPhrases.put("Where do you live?", "Uba he?");
        rwPhrases.put("What time is it?", "Ni isaha zingahe?");
        rwPhrases.put("I'm learning Kinyarwanda", "Ndiga Ikinyarwanda");
        rwPhrases.put("Do you speak Kinyarwanda?", "Uvuga Ikinyarwanda?");
        rwPhrases.put("I come from...", "Nkomoka...");
        rwPhrases.put("Where do you work?", "Ukora he?");
        rwPhrases.put("What do you do?", "Ukora iki?");
        rwPhrases.put("How old are you?", "Ufite imyaka ingahe?");
        rwPhrases.put("I am ... years old", "Mfite imyaka...");
        rwPhrases.put("Nice to meet you", "Nimerewe no guhura nawe");
        phrases.put("RW", rwPhrases);
        
        // Kiswahili phrases
        Map<String, String> swPhrases = new HashMap<>();
        swPhrases.put("I don't understand", "Sielewi");
        swPhrases.put("Please speak slowly", "Tafadhali sema polepole");
        swPhrases.put("What is your name?", "Jina lako ni nani?");
        swPhrases.put("My name is...", "Jina langu ni...");
        swPhrases.put("Where is the bathroom?", "Choo kiko wapi?");
        swPhrases.put("How much does this cost?", "Hii ni bei gani?");
        swPhrases.put("I am lost", "Nimepotea");
        swPhrases.put("Can you help me?", "Unaweza kunisaidia?");
        swPhrases.put("I am hungry", "Nina njaa");
        swPhrases.put("I am thirsty", "Nina kiu");
        phrases.put("SW", swPhrases);
        
        return phrases;
    }
    
    /**
     * Get color terms in different languages.
     * @return Map of language code to color terms
     */
    public static Map<String, Map<String, String>> getColors() {
        Map<String, Map<String, String>> colors = new HashMap<>();
        
        // Kinyarwanda colors - expanded
        Map<String, String> rwColors = new HashMap<>();
        rwColors.put("red", "Umutuku");
        rwColors.put("blue", "Ubururu");
        rwColors.put("green", "Icyatsi");
        rwColors.put("yellow", "Umuhondo");
        rwColors.put("black", "Umukara");
        rwColors.put("white", "Umweru");
        rwColors.put("brown", "Ikawa");
        rwColors.put("orange", "Icungucungu");
        rwColors.put("purple", "Umuhondo n'umutuku");
        rwColors.put("pink", "Urushyurabwoba");
        rwColors.put("gray", "Ubwoba");
        rwColors.put("silver", "Ifeza");
        rwColors.put("gold", "Zahabu");
        colors.put("RW", rwColors);
        
        // Kiswahili colors
        Map<String, String> swColors = new HashMap<>();
        swColors.put("red", "Nyekundu");
        swColors.put("blue", "Buluu");
        swColors.put("green", "Kijani");
        swColors.put("yellow", "Njano");
        swColors.put("black", "Nyeusi");
        swColors.put("white", "Nyeupe");
        swColors.put("brown", "Kahawia");
        swColors.put("orange", "Machungwa");
        swColors.put("purple", "Zambarau");
        swColors.put("pink", "Waridi");
        colors.put("SW", swColors);
        
        return colors;
    }
    
    /**
     * Get food and drink terms in different languages.
     * @return Map of language code to food and drink terms
     */
    public static Map<String, Map<String, String>> getFoodAndDrinks() {
        Map<String, Map<String, String>> foodAndDrinks = new HashMap<>();
        
        // Kinyarwanda food and drinks - extensive list
        Map<String, String> rwFoodAndDrinks = new HashMap<>();
        rwFoodAndDrinks.put("water", "Amazi");
        rwFoodAndDrinks.put("milk", "Amata");
        rwFoodAndDrinks.put("tea", "Icyayi");
        rwFoodAndDrinks.put("coffee", "Ikawa");
        rwFoodAndDrinks.put("juice", "Amashyushyu");
        rwFoodAndDrinks.put("beer", "Inzoga");
        rwFoodAndDrinks.put("bread", "Umugati");
        rwFoodAndDrinks.put("rice", "Umuceri");
        rwFoodAndDrinks.put("beans", "Ibinyomoro");
        rwFoodAndDrinks.put("meat", "Inyama");
        rwFoodAndDrinks.put("fish", "Amafi");
        rwFoodAndDrinks.put("chicken", "Inkoko");
        rwFoodAndDrinks.put("banana", "Igikoma");
        rwFoodAndDrinks.put("potato", "Ibirayi");
        rwFoodAndDrinks.put("vegetables", "Imboga");
        rwFoodAndDrinks.put("fruit", "Imbuto");
        rwFoodAndDrinks.put("egg", "Igi");
        rwFoodAndDrinks.put("salt", "Umunyu");
        rwFoodAndDrinks.put("sugar", "Isukari");
        rwFoodAndDrinks.put("oil", "Amavuta");
        rwFoodAndDrinks.put("onion", "Igitunguru");
        rwFoodAndDrinks.put("tomato", "Inyanya");
        foodAndDrinks.put("RW", rwFoodAndDrinks);
        
        // Kiswahili food and drinks
        Map<String, String> swFoodAndDrinks = new HashMap<>();
        swFoodAndDrinks.put("water", "Maji");
        swFoodAndDrinks.put("milk", "Maziwa");
        swFoodAndDrinks.put("tea", "Chai");
        swFoodAndDrinks.put("coffee", "Kahawa");
        swFoodAndDrinks.put("juice", "Juisi");
        swFoodAndDrinks.put("beer", "Bia");
        swFoodAndDrinks.put("bread", "Mkate");
        swFoodAndDrinks.put("rice", "Mchele");
        swFoodAndDrinks.put("beans", "Maharagwe");
        swFoodAndDrinks.put("meat", "Nyama");
        swFoodAndDrinks.put("fish", "Samaki");
        swFoodAndDrinks.put("chicken", "Kuku");
        swFoodAndDrinks.put("banana", "Ndizi");
        swFoodAndDrinks.put("potato", "Kiazi");
        swFoodAndDrinks.put("vegetables", "Mboga");
        foodAndDrinks.put("SW", swFoodAndDrinks);
        
        return foodAndDrinks;
    }
    
    /**
     * Get weather terms in different languages.
     * @return Map of language code to weather terms
     */
    public static Map<String, Map<String, String>> getWeatherTerms() {
        Map<String, Map<String, String>> weather = new HashMap<>();
        
        // Kinyarwanda weather terms - comprehensive
        Map<String, String> rwWeather = new HashMap<>();
        rwWeather.put("sunny", "Iziko");
        rwWeather.put("cloudy", "Ibicu");
        rwWeather.put("rainy", "Imvura");
        rwWeather.put("windy", "Umuyaga");
        rwWeather.put("cold", "Ubukonje");
        rwWeather.put("hot", "Ubushyuhe");
        rwWeather.put("snow", "Urubura"); // (rare in Rwanda)
        rwWeather.put("fog", "Igicu");
        rwWeather.put("storm", "Inkinganyanja");
        rwWeather.put("thunder", "Inkuba");
        rwWeather.put("lightning", "Umurabyo");
        rwWeather.put("drought", "Icyorezo");
        rwWeather.put("season", "Igihembwe");
        rwWeather.put("dry season", "Icyi");
        rwWeather.put("rainy season", "Itumba");
        weather.put("RW", rwWeather);
        
        // Kiswahili weather terms
        Map<String, String> swWeather = new HashMap<>();
        swWeather.put("sunny", "Jua");
        swWeather.put("cloudy", "Mawingu");
        swWeather.put("rainy", "Mvua");
        swWeather.put("windy", "Upepo");
        swWeather.put("cold", "Baridi");
        swWeather.put("hot", "Joto");
        swWeather.put("snow", "Theluji");
        swWeather.put("fog", "Ukungu");
        swWeather.put("storm", "Dhoruba");
        swWeather.put("thunder", "Radi");
        swWeather.put("lightning", "Umeme");
        weather.put("SW", swWeather);
        
        return weather;
    }
    
    /**
     * Get common verbs in different languages.
     * @return Map of language code to verb terms
     */
    public static Map<String, Map<String, String>> getCommonVerbs() {
        Map<String, Map<String, String>> verbs = new HashMap<>();
        
        // Kinyarwanda verbs - extensive list
        Map<String, String> rwVerbs = new HashMap<>();
        rwVerbs.put("to be", "Kuba");
        rwVerbs.put("to have", "Kugira");
        rwVerbs.put("to go", "Kujya");
        rwVerbs.put("to come", "Kuza");
        rwVerbs.put("to eat", "Kurya");
        rwVerbs.put("to drink", "Kunywa");
        rwVerbs.put("to sleep", "Gusinzira");
        rwVerbs.put("to speak", "Kuvuga");
        rwVerbs.put("to listen", "Kumva");
        rwVerbs.put("to read", "Gusoma");
        rwVerbs.put("to write", "Kwandika");
        rwVerbs.put("to see", "Kubona");
        rwVerbs.put("to know", "Kumenya");
        rwVerbs.put("to work", "Gukora");
        rwVerbs.put("to live", "Gubaho");
        rwVerbs.put("to learn", "Kwiga");
        rwVerbs.put("to teach", "Kwigisha");
        rwVerbs.put("to love", "Gukunda");
        rwVerbs.put("to help", "Gufasha");
        rwVerbs.put("to buy", "Kugura");
        rwVerbs.put("to sell", "Kugurisha");
        rwVerbs.put("to walk", "Kugenda");
        rwVerbs.put("to run", "Kwiruka");
        rwVerbs.put("to sit", "Kwicara");
        rwVerbs.put("to stand", "Guhaguruka");
        verbs.put("RW", rwVerbs);
        
        // Kiswahili verbs
        Map<String, String> swVerbs = new HashMap<>();
        swVerbs.put("to be", "Kuwa");
        swVerbs.put("to have", "Kuwa na");
        swVerbs.put("to go", "Kwenda");
        swVerbs.put("to come", "Kuja");
        swVerbs.put("to eat", "Kula");
        swVerbs.put("to drink", "Kunywa");
        swVerbs.put("to sleep", "Kulala");
        swVerbs.put("to speak", "Kusema");
        swVerbs.put("to listen", "Kusikiliza");
        swVerbs.put("to read", "Kusoma");
        swVerbs.put("to write", "Kuandika");
        swVerbs.put("to see", "Kuona");
        swVerbs.put("to know", "Kujua");
        swVerbs.put("to work", "Kufanya kazi");
        swVerbs.put("to live", "Kuishi");
        verbs.put("SW", swVerbs);
        
        return verbs;
    }
    
    /**
     * Get business and formal terms in different languages.
     * @return Map of language code to business terms
     */
    public static Map<String, Map<String, String>> getBusinessTerms() {
        Map<String, Map<String, String>> businessTerms = new HashMap<>();
        
        // Kinyarwanda business terms
        Map<String, String> rwBusiness = new HashMap<>();
        rwBusiness.put("business", "Ubucuruzi");
        rwBusiness.put("work", "Akazi");
        rwBusiness.put("office", "Ibiro");
        rwBusiness.put("meeting", "Inama");
        rwBusiness.put("manager", "Umuyobozi");
        rwBusiness.put("employee", "Umukozi");
        rwBusiness.put("customer", "Umukiriya");
        rwBusiness.put("money", "Amafaranga");
        rwBusiness.put("price", "Igiciro");
        rwBusiness.put("contract", "Amasezerano");
        rwBusiness.put("document", "Inyandiko");
        rwBusiness.put("computer", "Mudasobwa");
        rwBusiness.put("telephone", "Telefoni");
        rwBusiness.put("email", "Imeyili");
        rwBusiness.put("report", "Raporo");
        rwBusiness.put("project", "Umushinga");
        rwBusiness.put("deadline", "Igihe ntarengwa");
        rwBusiness.put("budget", "Ingengo y'imari");
        businessTerms.put("RW", rwBusiness);
        
        // Kiswahili business terms
        Map<String, String> swBusiness = new HashMap<>();
        swBusiness.put("business", "Biashara");
        swBusiness.put("work", "Kazi");
        swBusiness.put("office", "Ofisi");
        swBusiness.put("meeting", "Mkutano");
        swBusiness.put("manager", "Meneja");
        swBusiness.put("employee", "Mfanyakazi");
        swBusiness.put("customer", "Mteja");
        swBusiness.put("money", "Pesa");
        swBusiness.put("price", "Bei");
        businessTerms.put("SW", swBusiness);
        
        return businessTerms;
    }
    
    /**
     * Get educational terms in different languages.
     * @return Map of language code to educational terms
     */
    public static Map<String, Map<String, String>> getEducationalTerms() {
        Map<String, Map<String, String>> educationalTerms = new HashMap<>();
        
        // Kinyarwanda educational terms
        Map<String, String> rwEducation = new HashMap<>();
        rwEducation.put("school", "Ishuri");
        rwEducation.put("university", "Kaminuza");
        rwEducation.put("student", "Umunyeshuri");
        rwEducation.put("teacher", "Umwarimu");
        rwEducation.put("lesson", "Isomo");
        rwEducation.put("book", "Igitabo");
        rwEducation.put("pen", "Ikaramu");
        rwEducation.put("paper", "Impapuro");
        rwEducation.put("exam", "Ikizamini");
        rwEducation.put("homework", "Amakuru yo mu rugo");
        rwEducation.put("classroom", "Icyumba cy'ishuri");
        rwEducation.put("library", "Isomero");
        rwEducation.put("degree", "Impamyabumenyi");
        rwEducation.put("certificate", "Icyemezo");
        rwEducation.put("grade", "Amanota");
        educationalTerms.put("RW", rwEducation);
        
        // Kiswahili educational terms
        Map<String, String> swEducation = new HashMap<>();
        swEducation.put("school", "Shule");
        swEducation.put("university", "Chuo kikuu");
        swEducation.put("student", "Mwanafunzi");
        swEducation.put("teacher", "Mwalimu");
        swEducation.put("lesson", "Somo");
        swEducation.put("book", "Kitabu");
        swEducation.put("pen", "Kalamu");
        swEducation.put("paper", "Karatasi");
        swEducation.put("exam", "Mtihani");
        educationalTerms.put("SW", swEducation);
        
        return educationalTerms;
    }
    
    /**
     * Get months of the year in different languages.
     * @return Map of language code to months array - alias for getMonths()
     */
    public static Map<String, String[]> getMonthsOfYear() {
        return getMonths();
    }
    
    /**
     * Get past tense examples in different languages.
     * @return Map of language code to past tense examples
     */
    public static Map<String, Map<String, String>> getPastTenseExamples() {
        Map<String, Map<String, String>> pastTense = new HashMap<>();
        
        // Kinyarwanda past tense examples
        Map<String, String> rwPastTense = new HashMap<>();
        rwPastTense.put("I went", "Nagiye");
        rwPastTense.put("I ate", "Ndiye");
        rwPastTense.put("I spoke", "Navuze");
        rwPastTense.put("I saw", "Nabonye");
        rwPastTense.put("I learned", "Nize");
        rwPastTense.put("I worked", "Nakoze");
        rwPastTense.put("I came", "Naje");
        rwPastTense.put("I read", "Nasomye");
        rwPastTense.put("I wrote", "Nanditse");
        rwPastTense.put("I lived", "Nabayeho");
        pastTense.put("RW", rwPastTense);
        
        // Kiswahili past tense examples
        Map<String, String> swPastTense = new HashMap<>();
        swPastTense.put("I went", "Nilienda");
        swPastTense.put("I ate", "Nilikula");
        swPastTense.put("I spoke", "Nilisema");
        swPastTense.put("I saw", "Niliona");
        swPastTense.put("I learned", "Nilijifunza");
        swPastTense.put("I worked", "Nilifanya kazi");
        swPastTense.put("I came", "Nilikuja");
        swPastTense.put("I read", "Nilisoma");
        swPastTense.put("I wrote", "Niliandika");
        swPastTense.put("I lived", "Niliishi");
        pastTense.put("SW", swPastTense);
        
        return pastTense;
    }
    
    /**
     * Get future tense examples in different languages.
     * @return Map of language code to future tense examples
     */
    public static Map<String, Map<String, String>> getFutureTenseExamples() {
        Map<String, Map<String, String>> futureTense = new HashMap<>();
        
        // Kinyarwanda future tense examples
        Map<String, String> rwFutureTense = new HashMap<>();
        rwFutureTense.put("I will go", "Nzajya");
        rwFutureTense.put("I will eat", "Ndaza kurya");
        rwFutureTense.put("I will speak", "Nzavuga");
        rwFutureTense.put("I will see", "Nzabona");
        rwFutureTense.put("I will learn", "Nziga");
        rwFutureTense.put("I will work", "Nzakora");
        rwFutureTense.put("I will come", "Nzaza");
        rwFutureTense.put("I will read", "Nzasoma");
        rwFutureTense.put("I will write", "Nzandika");
        rwFutureTense.put("I will live", "Nzabaho");
        futureTense.put("RW", rwFutureTense);
        
        // Kiswahili future tense examples
        Map<String, String> swFutureTense = new HashMap<>();
        swFutureTense.put("I will go", "Nitaenda");
        swFutureTense.put("I will eat", "Nitakula");
        swFutureTense.put("I will speak", "Nitasema");
        swFutureTense.put("I will see", "Nitaona");
        swFutureTense.put("I will learn", "Nitajifunza");
        swFutureTense.put("I will work", "Nitafanya kazi");
        swFutureTense.put("I will come", "Nitakuja");
        swFutureTense.put("I will read", "Nitasoma");
        swFutureTense.put("I will write", "Nitaandika");
        swFutureTense.put("I will live", "Nitaishi");
        futureTense.put("SW", swFutureTense);
        
        return futureTense;
    }
    
    /**
     * Get direction phrases in different languages.
     * @return Map of language code to direction phrases
     */
    public static Map<String, Map<String, String>> getDirectionPhrases() {
        Map<String, Map<String, String>> directions = new HashMap<>();
        
        // Kinyarwanda direction phrases
        Map<String, String> rwDirections = new HashMap<>();
        rwDirections.put("left", "Ibumoso");
        rwDirections.put("right", "Iburyo");
        rwDirections.put("straight", "Hitaziguye");
        rwDirections.put("north", "Mumajyaruguru");
        rwDirections.put("south", "Mumajyepfo");
        rwDirections.put("east", "Muburasirazuba");
        rwDirections.put("west", "Muburengerazuba");
        rwDirections.put("here", "Hano");
        rwDirections.put("there", "Hariya");
        rwDirections.put("near", "Hafi");
        rwDirections.put("far", "Kure");
        rwDirections.put("up", "Hejuru");
        rwDirections.put("down", "Hepfo");
        directions.put("RW", rwDirections);
        
        // Kiswahili direction phrases
        Map<String, String> swDirections = new HashMap<>();
        swDirections.put("left", "Kushoto");
        swDirections.put("right", "Kulia");
        swDirections.put("straight", "Moja kwa moja");
        swDirections.put("north", "Kaskazini");
        swDirections.put("south", "Kusini");
        swDirections.put("east", "Mashariki");
        swDirections.put("west", "Magharibi");
        swDirections.put("here", "Hapa");
        swDirections.put("there", "Pale");
        swDirections.put("near", "Karibu");
        swDirections.put("far", "Mbali");
        directions.put("SW", swDirections);
        
        return directions;
    }
    
    /**
     * Get idioms and proverbs in different languages.
     * @return Map of language code to idioms and proverbs
     */
    public static Map<String, Map<String, String>> getIdiomsAndProverbs() {
        Map<String, Map<String, String>> idioms = new HashMap<>();
        
        // Kinyarwanda idioms and proverbs
        Map<String, String> rwIdioms = new HashMap<>();
        rwIdioms.put("Umuntu n'umuntu ku bantu", "A person is a person through other people (Ubuntu philosophy)");
        rwIdioms.put("Agaciro k'umuntu ni ubwoba", "The dignity of a person is respect");
        rwIdioms.put("Ubwoba bushya ubwoba bukuze", "New respect honors old respect");
        rwIdioms.put("Kwihangana biratunganya", "Patience leads to success");
        rwIdioms.put("Ubwoba bucya mu mutima", "Respect grows in the heart");
        rwIdioms.put("Inzira nziza yubahiriza", "A good path is one that respects others");
        rwIdioms.put("Ubwiyunge bw'umuntu ni ubucuti", "A person's wealth is friendship");
        rwIdioms.put("Ikibazo gikemura ikibazo", "One problem solves another");
        idioms.put("RW", rwIdioms);
        
        // Kiswahili idioms and proverbs
        Map<String, String> swIdioms = new HashMap<>();
        swIdioms.put("Haraka haraka haina baraka", "Hurry hurry has no blessing");
        swIdioms.put("Subira huvuta heri", "Patience brings good fortune");
        swIdioms.put("Ukuu hauongopi", "Greatness does not gossip");
        swIdioms.put("Mchagua jembe si mkulima", "One who chooses the hoe is not necessarily a farmer");
        swIdioms.put("Asiyesikia la mkuu huvunjika guu", "One who doesn't listen to the elder breaks their leg");
        idioms.put("SW", swIdioms);
        
        return idioms;
    }
}