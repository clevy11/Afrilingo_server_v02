package edtech.afrilingo.dataloader;

import edtech.afrilingo.auth.AuthenticationService;
import edtech.afrilingo.auth.RegisterRequest;
import edtech.afrilingo.course.Course;
import edtech.afrilingo.course.CourseRepository;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.language.LanguageRepository;
import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.lesson.LessonRepository;
import edtech.afrilingo.lesson.LessonType;
import edtech.afrilingo.lesson.content.ContentType;
import edtech.afrilingo.lesson.content.LessonContent;
import edtech.afrilingo.lesson.content.LessonContentRepository;
import edtech.afrilingo.profile.UserProfile;
import edtech.afrilingo.profile.UserProfileRepository;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.question.QuestionRepository;
import edtech.afrilingo.question.QuestionType;
import edtech.afrilingo.quiz.Quiz;
import edtech.afrilingo.quiz.QuizRepository;
import edtech.afrilingo.quiz.option.Option;
import edtech.afrilingo.quiz.option.OptionRepository;
import edtech.afrilingo.token.TokenRepository;
import edtech.afrilingo.user.Role;
import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.Map.Entry;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataLoaderService {

    private final LanguageRepository languageRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public void loadAllData() {
        try {
            loadLanguages();
            loadCourses();
            loadLessons();
            loadLessonContent();
            loadQuizzes();
            loadUsers();
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load all data: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.GENERAL_ERROR);
        }
    }

    @Transactional
    public void loadLanguages() {
        try {
            if (languageRepository.count() > 0) {
                return; // Skip if languages are already loaded
            }

            List<Language> languages = Arrays.asList(
                    Language.builder()
                            .name("Kinyarwanda")
                            .code("RW")
                            .description("Kinyarwanda is the official language of Rwanda, spoken by almost the entire population.")
                            .flagImage("https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Flag_of_Rwanda.svg/2560px-Flag_of_Rwanda.svg.png")
                            .build(),
                    Language.builder()
                            .name("Kiswahili")
                            .code("SW")
                            .description("Kiswahili is widely spoken across East Africa and is an official language in Kenya, Tanzania, and Uganda.")
                            .flagImage("https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/Flag_of_Kenya.svg/330px-Flag_of_Kenya.svg.png")
                            .build()
            );

            languageRepository.saveAll(languages);
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load languages: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.LANGUAGE_LOAD_ERROR);
        }
    }

    @Transactional
    public void loadCourses() {
        try {
            if (courseRepository.count() > 0) {
                return; // Skip if courses are already loaded
            }

            // Make sure languages are loaded first
            if (languageRepository.count() == 0) {
                loadLanguages();
            }

            // Get languages
            Language kinyarwanda = languageRepository.findByCode("RW").orElseThrow();
            Language kiswahili = languageRepository.findByCode("SW").orElseThrow();

            List<Course> courses = new ArrayList<>();

            // Kinyarwanda courses
            courses.add(Course.builder()
                    .title("Kinyarwanda for Beginners")
                    .description("Learn the basics of Kinyarwanda including greetings, numbers, and everyday phrases.")
                    .level("Beginner")
                    .image("kinyarwanda-beginner.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());


            // Additional comprehensive Kinyarwanda courses
            courses.add(Course.builder()
                    .title("Business Kinyarwanda")
                    .description("Professional Kinyarwanda for workplace communication, meetings, and formal presentations.")
                    .level("Intermediate")
                    .image("kinyarwanda-business.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            courses.add(Course.builder()
                    .title("Kinyarwanda Grammar Mastery")
                    .description("Comprehensive grammar course covering verb conjugations, noun classes, and complex sentence structures.")
                    .level("Advanced")
                    .image("kinyarwanda-grammar.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            courses.add(Course.builder()
                    .title("Conversational Kinyarwanda")
                    .description("Focus on practical conversation skills for daily interactions and social situations.")
                    .level("Intermediate")
                    .image("kinyarwanda-conversation.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            courses.add(Course.builder()
                    .title("Kinyarwanda for Healthcare")
                    .description("Medical and healthcare vocabulary in Kinyarwanda for professionals and patients.")
                    .level("Intermediate")
                    .image("kinyarwanda-healthcare.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            courses.add(Course.builder()
                    .title("Academic Kinyarwanda")
                    .description("Advanced Kinyarwanda for academic writing, research, and scholarly communication.")
                    .level("Advanced")
                    .image("kinyarwanda-academic.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            courses.add(Course.builder()
                    .title("Kinyarwanda Pronunciation Guide")
                    .description("Master correct pronunciation, tone, and accent patterns in Kinyarwanda.")
                    .level("Beginner")
                    .image("kinyarwanda-pronunciation.jpg")
                    .isActive(true)
                    .language(kinyarwanda)
                    .build());

            // Kiswahili courses
            courses.add(Course.builder()
                    .title("Kiswahili for Beginners")
                    .description("Start your journey in Kiswahili with basic vocabulary and simple conversations.")
                    .level("Beginner")
                    .image("kiswahili-beginner.jpg")
                    .isActive(true)
                    .language(kiswahili)
                    .build());

            // Excluded Intermediate and Advanced Kiswahili due to limited content



            courseRepository.saveAll(courses);
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load courses: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.COURSE_LOAD_ERROR);
        }
    }

    @Transactional
    public void loadLessons() {
        try {
            if (lessonRepository.count() > 0) {
                return; // Skip if lessons are already loaded
            }

            // Make sure courses are loaded first
            if (courseRepository.count() == 0) {
                loadCourses();
            }

            List<Course> courses = courseRepository.findAll();
            List<Lesson> lessons = new ArrayList<>();

            // For each course, create lessons
            for (Course course : courses) {
                String languageCode = course.getLanguage().getCode();
                String level = course.getLevel();
                String courseTitle = course.getTitle();

                // Lesson topics based on language, level, and course title
                List<LessonData> lessonDataList = getLessonDataForCourse(languageCode, level, courseTitle);

                for (int i = 0; i < lessonDataList.size(); i++) {
                    LessonData lessonData = lessonDataList.get(i);

                    // Idempotency: skip if lesson with same title already exists for this course
                    if (lessonRepository.existsByCourseIdAndTitle(course.getId(), lessonData.getTitle())) {
                        continue;
                    }

                    lessons.add(Lesson.builder()
                            .title(lessonData.getTitle())
                            .description(lessonData.getDescription())
                            .type(lessonData.getType())
                            .orderIndex(i)
                            .isRequired(true)
                            .course(course)
                            .build());
                }
            }

            lessonRepository.saveAll(lessons);
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load lessons: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.LESSON_LOAD_ERROR);
        }
    }

    @Transactional
    public void loadLessonContent() {
        try {
            if (lessonContentRepository.count() > 0) {
                return; // Skip if lesson content is already loaded
            }

            // Make sure lessons are loaded first
            if (lessonRepository.count() == 0) {
                loadLessons();
            }

            List<Lesson> lessons = lessonRepository.findAll();
            List<LessonContent> contents = new ArrayList<>();

            for (Lesson lesson : lessons) {
                // Get language code to customize content based on language
                String languageCode = lesson.getCourse().getLanguage().getCode();

                // Add text content
                contents.add(LessonContent.builder()
                        .contentType(ContentType.TEXT)
                        .contentData(generateTextContent(lesson.getTitle(), languageCode))
                        .lesson(lesson)
                        .build());

                // Add audio content (URL to audio file)
                contents.add(LessonContent.builder()
                        .contentType(ContentType.AUDIO)
                        .mediaUrl("https://assets.afrilingo.com/audio/" + languageCode.toLowerCase() + "/" +
                                lesson.getTitle().toLowerCase().replace(" ", "_") + ".mp3")
                        .lesson(lesson)
                        .build());

                // Add image content if appropriate
                if (lesson.getType() == LessonType.IMAGE_OBJECT) {
                    contents.add(LessonContent.builder()
                            .contentType(ContentType.IMAGE)
                            .mediaUrl("https://assets.afrilingo.com/images/" + languageCode.toLowerCase() + "/" +
                                    lesson.getTitle().toLowerCase().replace(" ", "_") + ".jpg")
                            .lesson(lesson)
                            .build());
                }
            }

            lessonContentRepository.saveAll(contents);
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load lesson content: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.LESSON_CONTENT_LOAD_ERROR);
        }
    }

    @Transactional
    public void loadQuizzes() {
        try {
            if (quizRepository.count() > 0) {
                return; // Skip if quizzes are already loaded
            }

            // Make sure lessons are loaded first
            if (lessonRepository.count() == 0) {
                loadLessons();
            }

            List<Lesson> lessons = lessonRepository.findAll();
            List<Quiz> quizzes = new ArrayList<>();

            // Create one quiz per lesson
            for (Lesson lesson : lessons) {
                Quiz quiz = Quiz.builder()
                        .title("Quiz: " + lesson.getTitle())
                        .description("Test your knowledge of " + lesson.getTitle())
                        .minPassingScore(70) // 70% to pass
                        .lesson(lesson)
                        .build();

                quizzes.add(quiz);
            }

            // Save all quizzes first
            quizRepository.saveAll(quizzes);

            // Now add questions to each quiz
            for (Quiz quiz : quizzes) {
                String languageCode = quiz.getLesson().getCourse().getLanguage().getCode();
                createQuestionsForQuiz(quiz, languageCode);
            }
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load quizzes: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.QUIZ_LOAD_ERROR);
        }
    }

    @Transactional
    public void loadUsers() {
        try {
            if (userRepository.count() > 0) {
                return; // Skip if users are already loaded
            }

            // Create admin user
            createUserWithProfile(
                "Admin",
                "User",
                "admin@gmail.com",
                "728728Clb@",
                Role.ROLE_ADMIN,
                "Rwanda",
                "English",
                "To help others learn African languages",
                null,
                true,
                60,
                "09:00",
                List.of("RW", "SW", "EN")
            );

            // Create regular users
            createUserWithProfile(
                "Buntu",
                "Levy Caleb",
                "buntulevycaleb@gmail.com",
                "728728Clb@",
                Role.ROLE_USER,
                "Rwanda",
                "Kinyarwanda",
                "To connect with my roots",
                null,
                true,
                30,
                "19:00",
                List.of("EN", "SW")
            );

            createUserWithProfile(
                "Jane",
                "Smith",
                "proctor@gmail.com",
                "728728Clb@",
                Role.ROLE_PROCTOR,
                "Kenya",
                "English",
                "Travel to East Africa",
                null,
                true,
                20,
                "08:00",
                List.of("SW", "RW")
            );

            createUserWithProfile(
                "Robert",
                "Johnson",
                "robert@example.com",
                "728728Clb@",
                Role.ROLE_USER,
                "South Africa",
                "English",
                "Business opportunities",
                null,
                false,
                15,
                "20:00",
                List.of("RW")
            );

            log.info("Successfully created {} users with profiles", userRepository.count());
        } catch (Exception e) {
            throw new DataLoaderException("Failed to load users: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.USER_LOAD_ERROR);
        }
    }

    @Transactional
    public void resetAllData() {
        try {
            // Delete all data in reverse order of dependencies
            tokenRepository.deleteAll();
            userProfileRepository.deleteAll();
            userRepository.deleteAll();
            optionRepository.deleteAll();
            questionRepository.deleteAll();
            quizRepository.deleteAll();
            lessonContentRepository.deleteAll();
            lessonRepository.deleteAll();
            courseRepository.deleteAll();
            languageRepository.deleteAll();
        } catch (Exception e) {
            throw new DataLoaderException("Failed to reset data: " + e.getMessage(),
                    e, DataLoaderException.DataLoaderErrorCode.DATA_RESET_ERROR);
        }
    }

    // Helper methods for generating sample data

    private List<LessonData> getLessonDataForCourse(String languageCode, String level, String courseTitle) {
        List<LessonData> lessonData = new ArrayList<>();

        // Tailor lessons to course title to avoid duplicating the same topics across all courses
        String title = courseTitle == null ? "" : courseTitle;
        boolean isGenericLevelCourse = title.contains("Beginner") || title.contains("Intermediate") || title.contains("Advanced");

        if (isGenericLevelCourse) {
            // Generic foundational topics for level-based courses
            lessonData.add(new LessonData("Greetings and Introductions",
                    "Learn how to greet people and introduce yourself", LessonType.AUDIO));
            lessonData.add(new LessonData("Numbers and Counting",
                    "Master numbers and basic counting", LessonType.AUDIO));
            lessonData.add(new LessonData("Family Members",
                    "Learn vocabulary for family relationships", LessonType.IMAGE_OBJECT));
            lessonData.add(new LessonData("Days and Months",
                    "Learn the names of days and months", LessonType.READING));
            lessonData.add(new LessonData("Common Phrases",
                    "Essential everyday expressions", LessonType.AUDIO));
        } else if (title.contains("Business")) {
            lessonData.add(new LessonData("Business Etiquette",
                    "Professional etiquette and introductions in business settings", LessonType.AUDIO));
            lessonData.add(new LessonData("Meetings and Presentations",
                    "Key phrases and structures for meetings", LessonType.READING));
            lessonData.add(new LessonData("Negotiation Vocabulary",
                    "Terms used in negotiation and contracts", LessonType.READING));
            lessonData.add(new LessonData("Customer Communication",
                    "Email and phone support communication", LessonType.AUDIO));
            lessonData.add(new LessonData("Workplace Scenarios",
                    "Role-play common workplace situations", LessonType.IMAGE_OBJECT));
        } else if (title.contains("Grammar")) {
            lessonData.add(new LessonData("Noun Classes",
                    "Deep dive into noun classes and agreement", LessonType.READING));
            lessonData.add(new LessonData("Verb Conjugations",
                    "Present, past, and future conjugations", LessonType.READING));
            lessonData.add(new LessonData("Complex Sentences",
                    "Subordinate clauses and connectors", LessonType.READING));
            lessonData.add(new LessonData("Common Errors",
                    "Avoiding frequent grammar mistakes", LessonType.AUDIO));
            lessonData.add(new LessonData("Practice Drills",
                    "Applied grammar exercises", LessonType.AUDIO));
        } else if (title.contains("Conversational")) {
            lessonData.add(new LessonData("Introductions and Small Talk",
                    "Natural dialogues for daily interactions", LessonType.AUDIO));
            lessonData.add(new LessonData("Asking for Help",
                    "Polite requests and clarifications", LessonType.AUDIO));
            lessonData.add(new LessonData("At the Market",
                    "Buying and bargaining", LessonType.IMAGE_OBJECT));
            lessonData.add(new LessonData("Directions and Transport",
                    "Moving around town", LessonType.AUDIO));
            lessonData.add(new LessonData("Health and Emergencies",
                    "Describing symptoms and getting help", LessonType.READING));
        } else if (title.contains("Healthcare")) {
            lessonData.add(new LessonData("At the Clinic",
                    "Registering and explaining symptoms", LessonType.AUDIO));
            lessonData.add(new LessonData("Medical Vocabulary",
                    "Body parts and common conditions", LessonType.READING));
            lessonData.add(new LessonData("Giving Instructions",
                    "Dosage and aftercare", LessonType.READING));
            lessonData.add(new LessonData("Emergency Situations",
                    "Urgent phrases and responses", LessonType.AUDIO));
            lessonData.add(new LessonData("Patient Dialogue",
                    "Doctor-patient role play", LessonType.IMAGE_OBJECT));
        } else if (title.contains("Academic")) {
            lessonData.add(new LessonData("Academic Writing",
                    "Structure and style", LessonType.READING));
            lessonData.add(new LessonData("Research Vocabulary",
                    "Terms for research and study", LessonType.READING));
            lessonData.add(new LessonData("Presentations",
                    "Presenting academic work", LessonType.AUDIO));
            lessonData.add(new LessonData("Reading Comprehension",
                    "Understanding scholarly texts", LessonType.READING));
            lessonData.add(new LessonData("Seminar Discussions",
                    "Participating in seminars", LessonType.AUDIO));
        } else if (title.contains("Pronunciation")) {
            lessonData.add(new LessonData("Vowels and Consonants",
                    "Core sound system", LessonType.AUDIO));
            lessonData.add(new LessonData("Tone and Stress",
                    "Prosody and rhythm", LessonType.AUDIO));
            lessonData.add(new LessonData("Minimal Pairs",
                    "Practice distinguishing sounds", LessonType.AUDIO));
            lessonData.add(new LessonData("Common Pitfalls",
                    "Correcting frequent mistakes", LessonType.READING));
            lessonData.add(new LessonData("Applied Practice",
                    "Reading and speaking practice", LessonType.AUDIO));
        } else {
            // Fallback minimal set to avoid duplication in unspecified courses
            lessonData.add(new LessonData("Course Introduction",
                    "Overview of objectives and outcomes", LessonType.READING));
            lessonData.add(new LessonData("Key Vocabulary",
                    "Core terms for this course", LessonType.AUDIO));
            lessonData.add(new LessonData("Practice Tasks",
                    "Applied activities", LessonType.IMAGE_OBJECT));
        }

        // Add level-specific lessons only for generic level courses (not for specialized titles)
        if (isGenericLevelCourse) {
            if ("Beginner".equals(level)) {
                lessonData.add(new LessonData("Colors",
                        "Learn basic color vocabulary", LessonType.IMAGE_OBJECT));
                lessonData.add(new LessonData("Food and Drinks",
                        "Essential vocabulary for ordering food and drinks", LessonType.IMAGE_OBJECT));
                lessonData.add(new LessonData("Simple Questions",
                        "How to ask basic questions", LessonType.AUDIO));
                lessonData.add(new LessonData("Weather Terms",
                        "Vocabulary to describe weather conditions", LessonType.READING));
                lessonData.add(new LessonData("Basic Verbs",
                        "Common verbs and their usage", LessonType.READING));
            } else if ("Intermediate".equals(level)) {
                lessonData.add(new LessonData("Past Tense",
                        "How to talk about past events", LessonType.READING));
                lessonData.add(new LessonData("Future Tense",
                        "Expressing future actions and events", LessonType.READING));
                lessonData.add(new LessonData("Giving Directions",
                        "Vocabulary and phrases for navigation", LessonType.AUDIO));
                lessonData.add(new LessonData("Shopping Conversations",
                        "Dialogue practice for shopping scenarios", LessonType.AUDIO));
                lessonData.add(new LessonData("Describing People",
                        "Vocabulary for physical appearance and personality", LessonType.IMAGE_OBJECT));
            } else if ("Advanced".equals(level)) {
                lessonData.add(new LessonData("Idioms and Proverbs",
                        "Common sayings and their meanings", LessonType.READING));
                lessonData.add(new LessonData("Complex Grammar",
                        "Advanced grammatical structures", LessonType.READING));
                lessonData.add(new LessonData("Cultural Nuances",
                        "Understanding cultural contexts in language", LessonType.READING));
                lessonData.add(new LessonData("Professional Communication",
                        "Language for workplace and formal settings", LessonType.AUDIO));
                lessonData.add(new LessonData("Literature and Poetry",
                        "Exploring literary expressions", LessonType.READING));
            }
        }

        // Add language-specific lessons (lightweight, not to overwhelm course-specific tailoring)
        if ("RW".equals(languageCode)) {
            lessonData.add(new LessonData("Kinyarwanda Culture",
                    "Important cultural notes", LessonType.READING));
        } else if ("SW".equals(languageCode)) {
            lessonData.add(new LessonData("Swahili Culture",
                    "Cultural practices and context", LessonType.READING));
        }

        return lessonData;
    }

    private String generateTextContent(String lessonTitle, String languageCode) {
        StringBuilder content = new StringBuilder();
        content.append("# ").append(lessonTitle).append("\n\n");

        if (lessonTitle.contains("Greetings")) {
            Map<String, String[]> greetings = LanguageContentHelper.getGreetings();
            String[] languageGreetings = greetings.get(languageCode);

            content.append("## Common Greetings\n\n");
            if (languageGreetings != null) {
                for (int i = 0; i < Math.min(6, languageGreetings.length); i++) {
                    content.append("* **").append(languageGreetings[i]).append("**");
                    if (languageCode.equals("RW")) {
                        switch (i) {
                            case 0 -> content.append(" - Hello (to multiple people)");
                            case 1 -> content.append(" - Hello (to one person)");
                            case 2 -> content.append(" - Good morning");
                            case 3 -> content.append(" - Good afternoon/evening");
                            case 4 -> content.append(" - How are you?");
                            case 5 -> content.append(" - I'm fine");
                        }
                    } else if (languageCode.equals("SW")) {
                        switch (i) {
                            case 0 -> content.append(" - Hello");
                            case 1 -> content.append(" - How are you?");
                            case 2 -> content.append(" - How are you? (more casual)");
                            case 3 -> content.append(" - Good/fine");
                            case 4 -> content.append(" - How are you? (formal)");
                            case 5 -> content.append(" - I am fine");
                        }
                    } else {
                        switch (i) {
                            case 0 -> content.append(" - General greeting");
                            case 1 -> content.append(" - Morning greeting");
                            case 2 -> content.append(" - Afternoon greeting");
                            case 3 -> content.append(" - Evening greeting");
                            case 4 -> content.append(" - Asking about someone's wellbeing");
                            case 5 -> content.append(" - Responding to 'How are you?'");
                        }
                    }
                    content.append("\n");
                }
            } else {
                content.append("* No greetings available for this language code: ").append(languageCode).append("\n");
            }

            content.append("\n## Introduction Phrases\n\n");

            Map<String, Map<String, String>> commonPhrases = LanguageContentHelper.getCommonPhrases();
            Map<String, String> phrases = commonPhrases.get(languageCode);

            if (phrases != null) {
                String myNameIs = phrases.get("My name is...");
                if (myNameIs != null) {
                    content.append("* **").append(myNameIs).append("** - Introducing yourself\n");
                }

                if (languageCode.equals("RW")) {
                    content.append("* **Nkomoka...** - I come from...\n");
                    content.append("* **Ni byiza kuguhura** - Nice to meet you\n");
                } else if (languageCode.equals("SW")) {
                    content.append("* **Ninatoka...** - I come from...\n");
                    content.append("* **Nimefurahi kukuona** - Nice to meet you\n");
                } else {
                    content.append("* **I'm from...** - Stating where you're from\n");
                    content.append("* **Nice to meet you** - Polite expression after introduction\n");
                }
            } else {
                content.append("* No introduction phrases available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Numbers")) {
            Map<String, String[]> numbers = LanguageContentHelper.getNumbers();
            String[] languageNumbers = numbers.get(languageCode);

            content.append("## Numbers 1-10\n\n");
            if (languageNumbers != null) {
                for (int i = 0; i < languageNumbers.length; i++) {
                    content.append("* **").append(languageNumbers[i]).append("** - ").append(i + 1).append("\n");
                }
            } else {
                content.append("* No number terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Family")) {
            Map<String, Map<String, String>> familyTerms = LanguageContentHelper.getFamilyTerms();
            Map<String, String> terms = familyTerms.get(languageCode);

            content.append("## Family Terms\n\n");
            if (terms != null) {
                for (Entry<String, String> entry : terms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No family terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Days")) {
            Map<String, String[]> days = LanguageContentHelper.getDaysOfWeek();
            String[] languageDays = days.get(languageCode);

            content.append("## Days of the Week\n\n");
            if (languageDays != null) {
                for (int i = 0; i < languageDays.length; i++) {
                    content.append("* **").append(languageDays[i]).append("**");
                    switch (i) {
                        case 0 -> content.append(" - Monday");
                        case 1 -> content.append(" - Tuesday");
                        case 2 -> content.append(" - Wednesday");
                        case 3 -> content.append(" - Thursday");
                        case 4 -> content.append(" - Friday");
                        case 5 -> content.append(" - Saturday");
                        case 6 -> content.append(" - Sunday");
                    }
                    content.append("\n");
                }
            } else {
                content.append("* No day terms available for this language code: ").append(languageCode).append("\n");
            }

            Map<String, String[]> months = LanguageContentHelper.getMonthsOfYear();
            String[] languageMonths = months.get(languageCode);

            content.append("\n## Months of the Year\n\n");
            if (languageMonths != null) {
                for (int i = 0; i < languageMonths.length; i++) {
                    content.append("* **").append(languageMonths[i]).append("**");
                    switch (i) {
                        case 0 -> content.append(" - January");
                        case 1 -> content.append(" - February");
                        case 2 -> content.append(" - March");
                        case 3 -> content.append(" - April");
                        case 4 -> content.append(" - May");
                        case 5 -> content.append(" - June");
                        case 6 -> content.append(" - July");
                        case 7 -> content.append(" - August");
                        case 8 -> content.append(" - September");
                        case 9 -> content.append(" - October");
                        case 10 -> content.append(" - November");
                        case 11 -> content.append(" - December");
                    }
                    content.append("\n");
                }
            } else {
                content.append("* No month terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Phrases")) {
            Map<String, Map<String, String>> commonPhrases = LanguageContentHelper.getCommonPhrases();
            Map<String, String> phrases = commonPhrases.get(languageCode);

            content.append("## Essential Phrases\n\n");
            if (phrases != null) {
                for (Entry<String, String> entry : phrases.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No phrases available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Colors")) {
            Map<String, Map<String, String>> colors = LanguageContentHelper.getColors();
            Map<String, String> colorTerms = colors.get(languageCode);

            content.append("## Color Terms\n\n");
            if (colorTerms != null) {
                for (Entry<String, String> entry : colorTerms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No color terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Food")) {
            Map<String, Map<String, String>> foodAndDrinks = LanguageContentHelper.getFoodAndDrinks();
            Map<String, String> terms = foodAndDrinks.get(languageCode);

            content.append("## Food and Drinks\n\n");
            if (terms != null) {
                for (Entry<String, String> entry : terms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No food and drink terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Weather")) {
            Map<String, Map<String, String>> weatherTerms = LanguageContentHelper.getWeatherTerms();
            Map<String, String> terms = weatherTerms.get(languageCode);

            content.append("## Weather Terms\n\n");
            if (terms != null) {
                for (Entry<String, String> entry : terms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No weather terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Verbs")) {
            Map<String, Map<String, String>> verbs = LanguageContentHelper.getCommonVerbs();
            Map<String, String> verbTerms = verbs.get(languageCode);

            content.append("## Common Verbs\n\n");
            if (verbTerms != null) {
                for (Entry<String, String> entry : verbTerms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No verb terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Past")) {
            Map<String, Map<String, String>> pastTense = LanguageContentHelper.getPastTenseExamples();
            Map<String, String> examples = pastTense.get(languageCode);

            content.append("## Past Tense Examples\n\n");
            if (examples != null) {
                for (Entry<String, String> entry : examples.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No past tense examples available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Future")) {
            Map<String, Map<String, String>> futureTense = LanguageContentHelper.getFutureTenseExamples();
            Map<String, String> examples = futureTense.get(languageCode);

            content.append("## Future Tense Examples\n\n");
            if (examples != null) {
                for (Entry<String, String> entry : examples.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No future tense examples available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Directions")) {
            Map<String, Map<String, String>> directions = LanguageContentHelper.getDirectionPhrases();
            Map<String, String> directionTerms = directions.get(languageCode);

            content.append("## Direction and Navigation Terms\n\n");
            if (directionTerms != null) {
                for (Entry<String, String> entry : directionTerms.entrySet()) {
                    content.append("* **").append(entry.getValue()).append("** - ").append(entry.getKey()).append("\n");
                }
            } else {
                content.append("* No direction terms available for this language code: ").append(languageCode).append("\n");
            }
        } else if (lessonTitle.contains("Idioms")) {
            Map<String, Map<String, String>> idioms = LanguageContentHelper.getIdiomsAndProverbs();
            Map<String, String> idiomList = idioms.get(languageCode);

            content.append("## Idioms and Proverbs\n\n");
            if (idiomList != null) {
                for (Entry<String, String> entry : idiomList.entrySet()) {
                    content.append("* **").append(entry.getKey()).append("** - ").append(entry.getValue()).append("\n");
                }
            } else {
                content.append("* No idioms available for this language code: ").append(languageCode).append("\n");
            }
        }
        else {
            // Generic content for other lesson types
            content.append("## Introduction\n\n");
            content.append("This lesson will introduce you to important vocabulary and concepts related to ").append(lessonTitle).append(".\n\n");
            content.append("## Key Vocabulary\n\n");
            content.append("* Term 1 - Definition\n");
            content.append("* Term 2 - Definition\n");
            content.append("* Term 3 - Definition\n\n");
            content.append("## Practice Exercises\n\n");
            content.append("1. Exercise 1 description\n");
            content.append("2. Exercise 2 description\n");
            content.append("3. Exercise 3 description\n\n");
            content.append("## Cultural Context\n\n");
            content.append("Understanding the cultural significance of this topic is important because...\n\n");
        }

        return content.toString();
    }

    private void createQuestionsForQuiz(Quiz quiz, String languageCode) {
        // Idempotency guard: if questions already exist for this quiz, do not recreate
        if (questionRepository.countByQuizId(quiz.getId()) > 0) {
            return;
        }

        List<Question> questions = new ArrayList<>();
        Lesson lesson = quiz.getLesson();
        String lessonTitle = lesson.getTitle();

        // Determine what kind of questions to create based on the lesson title
        if (lessonTitle.contains("Greetings")) {
            questions.addAll(createGreetingQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Numbers")) {
            questions.addAll(createNumberQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Family")) {
            questions.addAll(createFamilyQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Colors")) {
            questions.addAll(createColorQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Food")) {
            questions.addAll(createFoodQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Weather")) {
            questions.addAll(createWeatherQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Verbs")) {
            questions.addAll(createVerbQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Days")) {
            questions.addAll(createDaysMonthsQuestions(quiz, languageCode));
        } else if (lessonTitle.contains("Directions")) {
            questions.addAll(createDirectionQuestions(quiz, languageCode));
        } else {
            // Generic questions for other lesson types
            questions.addAll(createGenericQuestions(quiz, languageCode));
        }

        questionRepository.saveAll(questions);

        // Create options for each question
        for (Question question : questions) {
            createOptionsForQuestion(question, languageCode);
        }
    }

    private List<Question> createGreetingQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();

        if ("RW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("How do you say 'Hello' to multiple people in Kinyarwanda?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Mwaramutse' means 'Good afternoon'.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The phrase for 'My name is...' in Kinyarwanda is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            // Add more Kinyarwanda questions for certification
            Question q4 = Question.builder()
                    .questionText("Which of the following means 'water'?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q5 = Question.builder()
                    .questionText("'Imodoka' means 'car'.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();


            questions.add(q4);
            questions.add(q5);

            // Removed OPEN_ENDED from quizzes per requirement: quizzes are not open-ended
        } else if ("SW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("What is the common greeting in Kiswahili?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Habari gani?' is a formal greeting in Kiswahili.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The phrase for 'My name is...' in Kiswahili is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        } else {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("Which greeting would you use in the morning?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'How do you do?' is a common greeting in modern English.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The phrase for introducing where you're from is 'I'm _____...'")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        }

        return questions;
    }

    private List<Question> createNumberQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();

        if ("RW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("What is 'Five' in Kinyarwanda?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Umunani' means 'Seven' in Kinyarwanda.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The Kinyarwanda word for 'Ten' is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        } else if ("SW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("What is 'Three' in Kiswahili?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Sita' means 'Seven' in Kiswahili.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The Kiswahili word for 'Eight' is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        } else {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("Which of these is the number 9?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Twenty' is written as '20' in numerical form.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The English word for '7' is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        }

        return questions;
    }

    private List<Question> createFamilyQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();

        if ("RW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("What is 'Father' in Kinyarwanda?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Umukobwa' means 'Son' in Kinyarwanda.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The Kinyarwanda word for 'Mother' is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        } else if ("SW".equals(languageCode)) {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("What is 'Sister' in Kiswahili?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Baba' means 'Father' in Kiswahili.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("The Kiswahili word for 'Brother' is '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        } else {
            // Multiple choice question
            Question q1 = Question.builder()
                    .questionText("Which term refers to your mother's or father's sister?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // True/False question
            Question q2 = Question.builder()
                    .questionText("'Cousin' can refer to both male and female relatives.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            // Fill in the blank
            Question q3 = Question.builder()
                    .questionText("Your father's father is your '_____'.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);
        }

        return questions;
    }

    private List<Question> createColorQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, Map<String, String>> colors = LanguageContentHelper.getColors();
        Map<String, String> colorTerms = colors.get(languageCode);

        // Check if we have valid data for this language
        if (colorTerms == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("Which of these is a primary color?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("Blue and yellow mixed together make green.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("The color of the sky is usually _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("What is the word for 'red' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String colorTerm = "blue";
        if ("RW".equals(languageCode)) {
            colorTerm = "Ubururu";
        } else if ("SW".equals(languageCode)) {
            colorTerm = "Bluu";
        }

        Question q2 = Question.builder()
                .questionText("'" + colorTerm + "' means 'green' in " + getLanguageName(languageCode) + ".")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " word for 'yellow' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createFoodQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, Map<String, String>> foodTerms = LanguageContentHelper.getFoodAndDrinks();
        Map<String, String> terms = foodTerms.get(languageCode);

        // Check if we have valid data for this language
        if (terms == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("Which of these is a type of fruit?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("Rice is a type of vegetable.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("A common drink made from coffee beans is _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("What is the word for 'water' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String breadTerm = "bread";
        if ("RW".equals(languageCode)) {
            breadTerm = "Umugati";
        } else if ("SW".equals(languageCode)) {
            breadTerm = "Mkate";
        }

        Question q2 = Question.builder()
                .questionText("'" + breadTerm + "' means 'rice' in " + getLanguageName(languageCode) + ".")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " word for 'chicken' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createWeatherQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, Map<String, String>> weatherTerms = LanguageContentHelper.getWeatherTerms();
        Map<String, String> terms = weatherTerms.get(languageCode);

        // Check if we have valid data for this language
        if (terms == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("What is the weather like on a sunny day?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("Rain is a type of weather phenomenon.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("The weather condition with clouds covering the sky is _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("How do you say 'It's raining' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String coldTerm = "cold";
        if ("RW".equals(languageCode)) {
            coldTerm = "Harakonje";
        } else if ("SW".equals(languageCode)) {
            coldTerm = "Kuna baridi";
        }

        Question q2 = Question.builder()
                .questionText("'" + coldTerm + "' refers to hot weather in " + getLanguageName(languageCode) + ".")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " word for 'cloudy' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createVerbQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, Map<String, String>> verbs = LanguageContentHelper.getCommonVerbs();
        Map<String, String> verbTerms = verbs.get(languageCode);

        // Check if we have valid data for this language
        if (verbTerms == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("What is the verb for 'to run' in English?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("'To eat' is a verb in English.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("The verb for 'to walk' in English is _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("What is the verb for 'to eat' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String speakVerb = "to speak";
        if ("RW".equals(languageCode)) {
            speakVerb = "Kuvuga";
        } else if ("SW".equals(languageCode)) {
            speakVerb = "Kusema";
        }

        Question q2 = Question.builder()
                .questionText("'" + speakVerb + "' means 'to listen' in " + getLanguageName(languageCode) + ".")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " verb for 'to go' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createDaysMonthsQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, String[]> days = LanguageContentHelper.getDaysOfWeek();
        String[] dayTerms = days.get(languageCode);

        Map<String, String[]> months = LanguageContentHelper.getMonthsOfYear();
        String[] monthTerms = months.get(languageCode);

        // Check if we have valid data for this language
        if (dayTerms == null || monthTerms == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("Which day comes after Sunday?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("January is the first month of the year.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("The month that comes after April is _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("What is the word for 'Monday' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String januaryTerm = monthTerms[0];

        Question q2 = Question.builder()
                .questionText("'" + januaryTerm + "' is the month that comes after December.")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " word for 'Friday' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createDirectionQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        Map<String, Map<String, String>> directions = LanguageContentHelper.getDirectionPhrases();
        Map<String, String> directionTerms = directions.get(languageCode);

        // Check if we have valid data for this language
        if (directionTerms == null || directionTerms.get("north") == null) {
            // Create generic questions if language-specific data is not available
            Question q1 = Question.builder()
                    .questionText("Which direction is opposite to north?")
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q2 = Question.builder()
                    .questionText("East is the direction where the sun rises.")
                    .questionType(QuestionType.TRUE_FALSE)
                    .points(1)
                    .quiz(quiz)
                    .build();

            Question q3 = Question.builder()
                    .questionText("The direction opposite to west is _____.")
                    .questionType(QuestionType.FILL_BLANK)
                    .points(1)
                    .quiz(quiz)
                    .build();

            questions.add(q1);
            questions.add(q2);
            questions.add(q3);

            return questions;
        }

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("How do you say 'left' in " + getLanguageName(languageCode) + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        String northTerm = directionTerms.get("north");

        Question q2 = Question.builder()
                .questionText("'" + northTerm + "' means 'south' in " + getLanguageName(languageCode) + ".")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("The " + getLanguageName(languageCode) + " phrase for 'straight ahead' is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);

        return questions;
    }

    private List<Question> createGenericQuestions(Quiz quiz, String languageCode) {
        List<Question> questions = new ArrayList<>();
        String lessonTitle = quiz.getLesson().getTitle();

        // Multiple choice question
        Question q1 = Question.builder()
                .questionText("Which statement best relates to " + lessonTitle + "?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        // True/False question
        Question q2 = Question.builder()
                .questionText(lessonTitle + " is an important aspect of daily communication.")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        // Fill in the blank
        Question q3 = Question.builder()
                .questionText("A key concept in " + lessonTitle + " is '_____'.")
                .questionType(QuestionType.FILL_BLANK)
                .points(1)
                .quiz(quiz)
                .build();

        // Replace open-ended with additional multiple choice / true-false aligned to lesson
        Question q4 = Question.builder()
                .questionText("Which statement best summarizes a key idea from '" + lessonTitle + "'?")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .points(1)
                .quiz(quiz)
                .build();

        Question q5 = Question.builder()
                .questionText("The content of '" + lessonTitle + "' includes practical examples.")
                .questionType(QuestionType.TRUE_FALSE)
                .points(1)
                .quiz(quiz)
                .build();

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        questions.add(q4);
        questions.add(q5);

        return questions;
    }

    private void createOptionsForQuestion(Question question, String languageCode) {
        // Idempotency guard: if options already exist for this question, do not recreate
        if (optionRepository.countByQuestionId(question.getId()) > 0) {
            return;
        }

        List<Option> options = new ArrayList<>();
        QuestionType type = question.getQuestionType();
        String questionText = question.getQuestionText();

        if (type == QuestionType.MULTIPLE_CHOICE) {
            // Greeting questions - enhanced patterns
            if ((questionText.contains("Hello") || questionText.contains("hello")) && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Uraho").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Muraho").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Mwaramutse").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mwiriwe").isCorrect(false).question(question).build());
            } else if ((questionText.contains("greeting") || questionText.contains("greet")) && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Muraho").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Mwaramutse").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Amakuru").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mwicuze").isCorrect(false).question(question).build());
            } else if ((questionText.contains("greeting") || questionText.contains("greet")) && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Jambo").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Habari").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Nzuri").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Asante").isCorrect(false).question(question).build());
            } else if (questionText.contains("morning")) {
                options.add(Option.builder().optionText("Good morning").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Good afternoon").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Good evening").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Good night").isCorrect(false).question(question).build());
            } else if (questionText.contains("Five") && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Gatanu").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Kabiri").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Gatatu").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Gatandatu").isCorrect(false).question(question).build());
            } else if (questionText.contains("Three") && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Mbili").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Tatu").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Nne").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Tisa").isCorrect(false).question(question).build());
            } else if (questionText.contains("9")) {
                options.add(Option.builder().optionText("Nine").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Seven").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Eight").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Ten").isCorrect(false).question(question).build());
            } else if (questionText.contains("Father") && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Mama").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Papa").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Mukuru").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mushiki").isCorrect(false).question(question).build());
            } else if (questionText.contains("Sister") && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Kaka").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Dada").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Mama").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Baba").isCorrect(false).question(question).build());
            } else if (questionText.contains("aunt")) {
                options.add(Option.builder().optionText("Uncle").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Aunt").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Cousin").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Niece").isCorrect(false).question(question).build());
            } else if (questionText.contains("red")) {
                Map<String, Map<String, String>> colors = LanguageContentHelper.getColors();
                Map<String, String> colorTerms = colors.get(languageCode);

                options.add(Option.builder().optionText(colorTerms.get("red")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(colorTerms.get("blue")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(colorTerms.get("green")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(colorTerms.get("yellow")).isCorrect(false).question(question).build());
            } else if (questionText.contains("water")) {
                Map<String, Map<String, String>> foodTerms = LanguageContentHelper.getFoodAndDrinks();
                Map<String, String> terms = foodTerms.get(languageCode);

                options.add(Option.builder().optionText(terms.get("water")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(terms.get("milk")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("juice")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("tea")).isCorrect(false).question(question).build());
            } else if (questionText.contains("raining")) {
                Map<String, Map<String, String>> weatherTerms = LanguageContentHelper.getWeatherTerms();
                Map<String, String> terms = weatherTerms.get(languageCode);

                options.add(Option.builder().optionText(terms.get("rainy")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(terms.get("sunny")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("cloudy")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("windy")).isCorrect(false).question(question).build());
            } else if (questionText.contains("to eat")) {
                Map<String, Map<String, String>> verbs = LanguageContentHelper.getCommonVerbs();
                Map<String, String> verbTerms = verbs.get(languageCode);

                options.add(Option.builder().optionText(verbTerms.get("to eat")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(verbTerms.get("to drink")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(verbTerms.get("to speak")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(verbTerms.get("to see")).isCorrect(false).question(question).build());
            } else if (questionText.contains("Monday")) {
                Map<String, String[]> days = LanguageContentHelper.getDaysOfWeek();
                String[] dayTerms = days.get(languageCode);

                options.add(Option.builder().optionText(dayTerms[0]).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(dayTerms[1]).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(dayTerms[2]).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(dayTerms[3]).isCorrect(false).question(question).build());
            } else if (questionText.contains("left")) {
                Map<String, Map<String, String>> directions = LanguageContentHelper.getDirectionPhrases();
                Map<String, String> directionTerms = directions.get(languageCode);

                options.add(Option.builder().optionText(directionTerms.get("left")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(directionTerms.get("right")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(directionTerms.get("straight")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(directionTerms.get("up")).isCorrect(false).question(question).build());
            } else if (questionText.contains("teacher") || questionText.contains("Teacher")) {
                if ("RW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Umwarimu").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Umunyeshuri").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umukozi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umuyobozi").isCorrect(false).question(question).build());
                } else if ("SW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Mwalimu").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Mwanafunzi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Mfanyakazi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Meneja").isCorrect(false).question(question).build());
                }
            } else if (questionText.contains("car") || questionText.contains("Car")) {
                if ("RW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Imodoka").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Ikinyabiziga").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Indege").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Ubwato").isCorrect(false).question(question).build());
                } else if ("SW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Gari").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Baiskeli").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Ndege").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Meli").isCorrect(false).question(question).build());
                }
            } else if (questionText.contains("book") || questionText.contains("Book")) {
                if ("RW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Igitabo").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Ikaramu").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Impapuro").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Ikibaho").isCorrect(false).question(question).build());
                } else if ("SW".equals(languageCode)) {
                    options.add(Option.builder().optionText("Kitabu").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Kalamu").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Karatasi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Meza").isCorrect(false).question(question).build());
                }
            
            // Business-related questions
            } else if ((questionText.contains("Business") || questionText.contains("business")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Essential for professional communication").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only used in markets").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not important for work").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for formal meetings").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Professional courtesy").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Market pricing").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Food preparation").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Weather patterns").isCorrect(false).question(question).build());
            } else {
                    options.add(Option.builder().optionText("Ubucuruzi").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Amazi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                }
            
            // Grammar-related questions  
            } else if ((questionText.contains("Grammar") || questionText.contains("grammar")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Foundation of proper communication").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only for writing").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not needed for speaking").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for formal situations").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Sentence structure").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Food types").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Weather conditions").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Market goods").isCorrect(false).question(question).build());
                } else {
                    options.add(Option.builder().optionText("Ikinyamakuru").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Ibirayi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Amazi").isCorrect(false).question(question).build());
                }
            
            // Conversational-related questions
            } else if ((questionText.contains("Conversational") || questionText.contains("conversation")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Essential for daily interactions").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only for formal events").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not important in modern times").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for elderly people").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Natural dialogue flow").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Cooking methods").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Transportation modes").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Building materials").isCorrect(false).question(question).build());
                } else {
                    options.add(Option.builder().optionText("Ikiganiro").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Amazi").isCorrect(false).question(question).build());
                }
            
            // Healthcare-related questions
            } else if ((questionText.contains("Healthcare") || questionText.contains("health")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Critical for medical communication").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only for doctors").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not necessary for patients").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for emergencies").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Clear symptom description").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Food recipes").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Dance moves").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Sports rules").isCorrect(false).question(question).build());
                } else {
                    options.add(Option.builder().optionText("Ubuzima").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Amazi").isCorrect(false).question(question).build());
                }
            
            // Academic-related questions
            } else if ((questionText.contains("Academic") || questionText.contains("academic")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Essential for educational success").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only for university students").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not needed for learning").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for research").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Structured learning approach").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Kitchen utensils").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Garden plants").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Clothing styles").isCorrect(false).question(question).build());
                } else {
                    options.add(Option.builder().optionText("Ubumenyi").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Ibirayi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                }
            
            // Pronunciation-related questions
            } else if ((questionText.contains("Pronunciation") || questionText.contains("pronunciation")) && "RW".equals(languageCode)) {
                if (questionText.contains("best relates") || questionText.contains("statement")) {
                    options.add(Option.builder().optionText("Fundamental for clear communication").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Only matters for singers").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Not important for understanding").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Only for teachers").isCorrect(false).question(question).build());
                } else if (questionText.contains("key concept")) {
                    options.add(Option.builder().optionText("Correct sound production").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Farming techniques").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Market prices").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Weather forecast").isCorrect(false).question(question).build());
                } else {
                    options.add(Option.builder().optionText("Imvugo").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Amazi").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                }
            
            } else {
                // Improved fallback for unmatched questions based on language
                if ("RW".equals(languageCode)) {
                    // Kinyarwanda vocabulary fallback
                    options.add(Option.builder().optionText("Amazi").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Umugati").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Inkoko").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Ibirayi").isCorrect(false).question(question).build());
                } else if ("SW".equals(languageCode)) {
                    // Kiswahili vocabulary fallback
                    options.add(Option.builder().optionText("Maji").isCorrect(true).question(question).build());
                    options.add(Option.builder().optionText("Mkate").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Kuku").isCorrect(false).question(question).build());
                    options.add(Option.builder().optionText("Kiazi").isCorrect(false).question(question).build());
                } else {
                    // Default fallback (should rarely be used now)
                options.add(Option.builder().optionText("Option A").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Option B").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Option C").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Option D").isCorrect(false).question(question).build());
                }
            }
        } else if (type == QuestionType.TRUE_FALSE) {
            // For True/False questions
            if (questionText.contains("'Mwaramutse' means 'Good afternoon'") ||
                    questionText.contains("'Habari gani?' is a formal greeting") ||
                    questionText.contains("'How do you do?' is a common greeting") ||
                    questionText.contains("'Umunani' means 'Seven'") ||
                    questionText.contains("'Sita' means 'Seven'") ||
                    questionText.contains("'Umukobwa' means 'Son'") ||
                    questionText.contains("refers to hot weather") ||
                    questionText.contains("means 'to listen'") ||
                    questionText.contains("means 'south'") ||
                    questionText.contains("that comes after December")) {

                options.add(Option.builder().optionText("True").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("False").isCorrect(true).question(question).build());
            } else {
                options.add(Option.builder().optionText("True").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("False").isCorrect(false).question(question).build());
            }
        } else if (type == QuestionType.FILL_BLANK) {
            // For Fill in the blank questions
            if (questionText.contains("'My name is...'") && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Nitwa").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Nkomoka").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mwirirwe").isCorrect(false).question(question).build());
            } else if (questionText.contains("'My name is...'") && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Jina langu ni").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Ninatoka").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Habari yangu").isCorrect(false).question(question).build());
            } else if (questionText.contains("I'm _____")) {
                options.add(Option.builder().optionText("from").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("called").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("saying").isCorrect(false).question(question).build());
            } else if (questionText.contains("'Ten'") && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Icumi").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Cumi").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Gatanu").isCorrect(false).question(question).build());
            } else if (questionText.contains("'Eight'") && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Nane").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Sita").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Tisa").isCorrect(false).question(question).build());
            } else if (questionText.contains("'7'")) {
                options.add(Option.builder().optionText("Seven").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Five").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Six").isCorrect(false).question(question).build());
            } else if (questionText.contains("'Mother'") && "RW".equals(languageCode)) {
                options.add(Option.builder().optionText("Mama").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Mushiki").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mukobwa").isCorrect(false).question(question).build());
            } else if (questionText.contains("'Brother'") && "SW".equals(languageCode)) {
                options.add(Option.builder().optionText("Kaka").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Dada").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Mjomba").isCorrect(false).question(question).build());
            } else if (questionText.contains("grandfather")) {
                options.add(Option.builder().optionText("Grandfather").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Cousin").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Uncle").isCorrect(false).question(question).build());
            } else if (questionText.contains("'yellow'")) {
                Map<String, Map<String, String>> colors = LanguageContentHelper.getColors();
                Map<String, String> colorTerms = colors.get(languageCode);

                options.add(Option.builder().optionText(colorTerms.get("yellow")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(colorTerms.get("green")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(colorTerms.get("orange")).isCorrect(false).question(question).build());
            } else if (questionText.contains("'chicken'")) {
                Map<String, Map<String, String>> foodTerms = LanguageContentHelper.getFoodAndDrinks();
                Map<String, String> terms = foodTerms.get(languageCode);

                options.add(Option.builder().optionText(terms.get("chicken")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(terms.get("meat")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("fish")).isCorrect(false).question(question).build());
            } else if (questionText.contains("'cloudy'")) {
                Map<String, Map<String, String>> weatherTerms = LanguageContentHelper.getWeatherTerms();
                Map<String, String> terms = weatherTerms.get(languageCode);

                options.add(Option.builder().optionText(terms.get("cloudy")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(terms.get("rainy")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(terms.get("windy")).isCorrect(false).question(question).build());
            } else if (questionText.contains("'to go'")) {
                Map<String, Map<String, String>> verbs = LanguageContentHelper.getCommonVerbs();
                Map<String, String> verbTerms = verbs.get(languageCode);

                options.add(Option.builder().optionText(verbTerms.get("to go")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(verbTerms.get("to come")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(verbTerms.get("to walk")).isCorrect(false).question(question).build());
            } else if (questionText.contains("'Friday'")) {
                Map<String, String[]> days = LanguageContentHelper.getDaysOfWeek();
                String[] dayTerms = days.get(languageCode);

                options.add(Option.builder().optionText(dayTerms[4]).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(dayTerms[3]).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(dayTerms[5]).isCorrect(false).question(question).build());
            } else if (questionText.contains("'straight ahead'")) {
                Map<String, Map<String, String>> directions = LanguageContentHelper.getDirectionPhrases();
                Map<String, String> directionTerms = directions.get(languageCode);

                options.add(Option.builder().optionText(directionTerms.get("straight ahead")).isCorrect(true).question(question).build());
                options.add(Option.builder().optionText(directionTerms.get("left")).isCorrect(false).question(question).build());
                options.add(Option.builder().optionText(directionTerms.get("right")).isCorrect(false).question(question).build());
            } else {
                // Generic options for other fill in the blank questions
                options.add(Option.builder().optionText("Answer 1").isCorrect(true).question(question).build());
                options.add(Option.builder().optionText("Answer 2").isCorrect(false).question(question).build());
                options.add(Option.builder().optionText("Answer 3").isCorrect(false).question(question).build());
            }
        }

        optionRepository.saveAll(options);
    }

    // Helper method to get language name from code
    private String getLanguageName(String languageCode) {
        return switch (languageCode) {
            case "RW" -> "Kinyarwanda";
            case "SW" -> "Kiswahili";

            default -> "Unknown Language";
        };
    }

    /**
     * Helper method to create a user with a profile in a single transaction
     */
    @Transactional
    public User createUserWithProfile(
            String firstName,
            String lastName,
            String email,
            String password,
            Role role,
            String country,
            String firstLanguage,
            String reasonToLearn,
            String profilePicture,
            boolean dailyReminders,
            int dailyGoalMinutes,
            String preferredLearningTime,
            List<String> languageCodes) {

        // Create user
        RegisterRequest request = RegisterRequest.builder()
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .password(password)
                .role(role)
                .build();

        // Register user (this will save the user)
        authenticationService.register(request);

        // Get the created user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Failed to find created user: " + email));

        // Create and save user profile
        createUserProfile(user, country, firstLanguage, reasonToLearn,
                profilePicture, dailyReminders, dailyGoalMinutes,
                preferredLearningTime, languageCodes);

        return user;
    }

    /**
     * Helper method to create a user profile
     */
    @Transactional
    public void createUserProfile(
            User user,
            String country,
            String firstLanguage,
            String reasonToLearn,
            String profilePicture,
            boolean dailyReminders,
            int dailyGoalMinutes,
            String preferredLearningTime,
            List<String> languageCodes) {

        // Check if profile already exists
        // Use user.getId() with findByUserId
        if (userProfileRepository.findByUserId(user.getId()).isPresent()) {
            return; // Skip if profile already exists
        }

        // Get languages to learnt
        // Implement findByCodeIn manually since it doesn't exist
        List<Language> languagesToLearn = new ArrayList<>();
        for (String code : languageCodes) {
            languageRepository.findByCode(code).ifPresent(languagesToLearn::add);
        }

        if (languagesToLearn.isEmpty()) {
            log.warn("No valid language codes found for user: {}", user.getEmail());
            return;
        }

        // Create and save profile
        UserProfile profile = UserProfile.builder()
                .user(user)
                .country(country)
                .firstLanguage(firstLanguage)
                .reasonToLearn(reasonToLearn)
                .profilePicture(profilePicture)
                .languagesToLearn(languagesToLearn)
                .dailyReminders(dailyReminders)
                .dailyGoalMinutes(dailyGoalMinutes)
                .preferredLearningTime(preferredLearningTime)
                .build();

        userProfileRepository.save(profile);
        log.info("Created profile for user: {}", user.getEmail());
    }

    // Helper class for lesson data
    class LessonData {
        private final String title;
        private final String description;
        private final LessonType type;

        public LessonData(String title, String description, LessonType type) {
            this.title = title;
            this.description = description;
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public LessonType getType() {
            return type;
        }
    }
}

