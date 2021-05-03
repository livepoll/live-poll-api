package de.livepoll.api.controller.v1

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*
import de.livepoll.api.repository.*
import de.livepoll.api.service.PollItemService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@RunWith(SpringRunner::class)
@DisplayName("Test poll item service")
class PollItemServiceTests {

    @Autowired
    private lateinit var pollItemService: PollItemService

    @MockBean
    private lateinit var pollItemRepository: PollItemRepository<PollItem>

    @MockBean
    private lateinit var pollRepository: PollRepository

    @MockBean
    private lateinit var multipleChoiceItemRepository: MultipleChoiceItemRepository

    @MockBean
    private lateinit var multipleChoiceItemAnswerRepository: MultipleChoiceItemAnswerRepository

    @MockBean
    private lateinit var openTextItemRepository: OpenTextItemRepository

    @MockBean
    private lateinit var quizItemRepository: QuizItemRepository

    @MockBean
    private lateinit var quizItemAnswerRepository: QuizItemAnswerRepository

    // Needed, since we can't use @Autowired in unit tests
    // If you know a solution to this "hack", please fix it or open a GitHub issue
    @TestConfiguration
    class PollitemServiceBean {
        @Bean
        fun pollItemService() = PollItemService()
    }

    // -------------------------------------- Init mock data -----------------------------------------------------------
    private val mockUser = User(
        0,
        "junit-tester",
        "junit@live-poll.de",
        "abc",
        true,
        "TESTER",
        emptyList()
    )

    private val mockPoll = Poll(
        0,
        mockUser,
        "Test poll",
        GregorianCalendar(2021, 5, 2).time,
        GregorianCalendar(2021, 5, 3).time,
        "test-poll",
        0,
        mutableListOf()
    )

    // Different poll item types
    private val testDataMultipleChoice = getTestDataForMultipleChoice()
    private val assertDataMultipleChoice = getAssertDataForMultipleChoice()
    private val testDataQuiz = getTestDataForQuiz()
    private val assertDataQuiz = getAssertDataForQuiz()
    private val testDataOpenText = getTestDataForOpenText()
    private val assertDataOpenText = getAssertDataForOpenText()

    @Before
    fun init() {
        // Setup fake function calls

        // Multiple Choice
        Mockito.`when`(pollItemRepository.findById(0)).thenReturn(Optional.of(testDataMultipleChoice[0]))
        Mockito.`when`(pollItemRepository.findById(1)).thenReturn(Optional.of(testDataMultipleChoice[1]))
        Mockito.`when`(multipleChoiceItemRepository.findById(0)).thenReturn(Optional.of(testDataMultipleChoice[0]))
        Mockito.`when`(multipleChoiceItemRepository.findById(1)).thenReturn(Optional.of(testDataMultipleChoice[1]))

        // Quiz
        Mockito.`when`(pollItemRepository.findById(3)).thenReturn(Optional.of(testDataQuiz[0]))
        Mockito.`when`(pollItemRepository.findById(4)).thenReturn(Optional.of(testDataQuiz[1]))
        Mockito.`when`(quizItemRepository.findById(3)).thenReturn(Optional.of(testDataQuiz[0]))
        Mockito.`when`(quizItemRepository.findById(4)).thenReturn(Optional.of(testDataQuiz[1]))

        // Open text
        Mockito.`when`(pollItemRepository.findById(5)).thenReturn(Optional.of(testDataOpenText[0]))
        Mockito.`when`(pollItemRepository.findById(6)).thenReturn(Optional.of(testDataOpenText[1]))
        Mockito.`when`(openTextItemRepository.findById(5)).thenReturn(Optional.of(testDataOpenText[0]))
        Mockito.`when`(openTextItemRepository.findById(6)).thenReturn(Optional.of(testDataOpenText[1]))
    }


    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------ Test Cases + Mock data per poll item type ----------------------------------------
    // -----------------------------------------------------------------------------------------------------------------


    // ----------------------------------------- Multiple Choice -------------------------------------------------------
    private fun getTestDataForMultipleChoice(): List<MultipleChoiceItem> {
        // Shell
        val multipleChoice1 = MultipleChoiceItem(
            0,
            mockPoll,
            0,
            "Multiple Choice Question1",
            false,
            false,
            mutableListOf()
        )
        val multipleChoice2 = MultipleChoiceItem(
            1,
            mockPoll,
            1,
            "Multiple Choice Question 2",
            false,
            false,
            mutableListOf()
        )

        // Selection options
        val answer11 = MultipleChoiceItemAnswer(0, multipleChoice1, "Option1-1", 0)
        val answer12 = MultipleChoiceItemAnswer(1, multipleChoice1, "Option1-2", 0)
        val answer13 = MultipleChoiceItemAnswer(2, multipleChoice1, "Option1-3", 0)
        val answer14 = MultipleChoiceItemAnswer(3, multipleChoice1, "Option1-4", 0)
        multipleChoice1.answers = mutableListOf(answer11, answer12, answer13, answer14)

        val answer21 = MultipleChoiceItemAnswer(4, multipleChoice1, "Option2-1", 0)
        val answer22 = MultipleChoiceItemAnswer(5, multipleChoice1, "Option2-2", 0)
        multipleChoice2.answers = mutableListOf(answer21, answer22)

        return listOf(multipleChoice1, multipleChoice2)
    }

    private fun getAssertDataForMultipleChoice(): List<MultipleChoiceItemDtoOut> {
        // Selection options
        val answer11 = MultipleChoiceItemAnswerDtoOut(0, "Option1-1", 0)
        val answer12 = MultipleChoiceItemAnswerDtoOut(1, "Option1-2", 0)
        val answer13 = MultipleChoiceItemAnswerDtoOut(2, "Option1-3", 0)
        val answer14 = MultipleChoiceItemAnswerDtoOut(3, "Option1-4", 0)
        val answers1 = listOf(answer11, answer12, answer13, answer14)

        val answer21 = MultipleChoiceItemAnswerDtoOut(4, "Option2-1", 0)
        val answer22 = MultipleChoiceItemAnswerDtoOut(5, "Option2-2", 0)
        val answers2 = listOf(answer21, answer22)

        // Shell
        val multipleChoice1 = MultipleChoiceItemDtoOut(
            0,
            mockPoll.id,
            "Multiple Choice Question1",
            0,
            PollItemType.MULTIPLE_CHOICE.representation,
            answers1
        )
        val multipleChoice2 = MultipleChoiceItemDtoOut(
            1,
            mockPoll.id,
            "Multiple Choice Question 2",
            1,
            PollItemType.MULTIPLE_CHOICE.representation,
            answers2
        )

        return listOf(multipleChoice1, multipleChoice2)
    }

    @Test
    @DisplayName("Get a single multiple choice item")
    fun testGetPollItemMultipleChoice() {
        val result1 = pollItemService.getPollItem(0)
        val expected1 = assertDataMultipleChoice[0]
        assertThat(result1).usingRecursiveComparison().isEqualTo(expected1)

        val result2 = pollItemService.getPollItem(1)
        val expected2 = assertDataMultipleChoice[1]
        assertThat(result2).usingRecursiveComparison().isEqualTo(expected2)
    }


    // --------------------------------------------- Quiz --------------------------------------------------------------
    private fun getTestDataForQuiz(): List<QuizItem> {
        // Shell
        val quiz1 = QuizItem(
            3,
            mockPoll,
            0,
            "Quiz Question1",
            mutableListOf()
        )

        val quiz2 = QuizItem(
            4,
            mockPoll,
            1,
            "Quiz Question2",
            mutableListOf()
        )

        // Selection options
        val answer11 = QuizItemAnswer(0, quiz1, "Option 1-1", true, 0)
        val answer12 = QuizItemAnswer(1, quiz1, "Option 1-2", false, 0)
        val answer13 = QuizItemAnswer(2, quiz1, "Option 1-3", false, 0)
        val answer14 = QuizItemAnswer(3, quiz1, "Option 1-4", false, 0)
        quiz1.answers = mutableListOf(answer11, answer12, answer13, answer14)


        val answer21 = QuizItemAnswer(4, quiz1, "Option 2-1", false, 0)
        val answer22 = QuizItemAnswer(5, quiz1, "Option 2-2", true, 0)
        quiz2.answers = mutableListOf(answer21, answer22)

        return listOf(quiz1, quiz2)
    }

    private fun getAssertDataForQuiz(): List<QuizItemDtoOut> {
        // Selection options
        val answer11 = QuizItemAnswerDtoOut(0, "Option 1-1", true, 0)
        val answer12 = QuizItemAnswerDtoOut(1, "Option 1-2", false, 0)
        val answer13 = QuizItemAnswerDtoOut(2, "Option 1-3", false, 0)
        val answer14 = QuizItemAnswerDtoOut(3, "Option 1-4", false, 0)
        val answers1 = listOf(answer11, answer12, answer13, answer14)

        val answer21 = QuizItemAnswerDtoOut(4, "Option 2-1", false, 0)
        val answer22 = QuizItemAnswerDtoOut(5, "Option 2-2", true, 0)
        val answers2 = listOf(answer21, answer22)

        // Shell
        val quiz1 = QuizItemDtoOut(
            3,
            mockPoll.id,
            "Quiz Question1",
            0,
            PollItemType.QUIZ.representation,
            answers1
        )
        val quiz2 = QuizItemDtoOut(
            4,
            mockPoll.id,
            "Quiz Question2",
            1,
            PollItemType.QUIZ.representation,
            answers2
        )

        return listOf(quiz1, quiz2)
    }

    @Test
    @DisplayName("Get a single quiz item")
    fun testGetPollItemQuiz() {
        val result1 = pollItemService.getPollItem(3)
        val expected1 = assertDataQuiz[0]
        assertThat(result1).usingRecursiveComparison().isEqualTo(expected1)

        val result2 = pollItemService.getPollItem(4)
        val expected2 = assertDataQuiz[1]
        assertThat(result2).usingRecursiveComparison().isEqualTo(expected2)
    }


    // ----------------------------------------- Open text -------------------------------------------------------------
    private fun getTestDataForOpenText(): List<OpenTextItem> {
        val openText1 = OpenTextItem(
            5,
            mockPoll,
            "Open text question1",
            0,
            mutableListOf()
        )

        val openText2 = OpenTextItem(
            6,
            mockPoll,
            "Open text question2",
            1,
            mutableListOf()
        )

        return listOf(openText1, openText2)
    }

    private fun getAssertDataForOpenText(): List<OpenTextItemDtoOut> {
        val openText1 = OpenTextItemDtoOut(
            5,
            mockPoll.id,
            "Open text question1",
            0,
            PollItemType.OPEN_TEXT.representation,
            emptyList()
        )

        val openText2 = OpenTextItemDtoOut(
            6,
            mockPoll.id,
            "Open text question2",
            1,
            PollItemType.OPEN_TEXT.representation,
            emptyList()
        )

        return listOf(openText1, openText2)
    }

    @Test
    @DisplayName("Get a single open text item")
    fun testGetPollOpenText() {
        val result1 = pollItemService.getPollItem(5)
        val expected1 = assertDataOpenText[0]
        assertThat(result1).usingRecursiveComparison().isEqualTo(expected1)

        val result2 = pollItemService.getPollItem(6)
        val expected2 = assertDataOpenText[1]
        assertThat(result2).usingRecursiveComparison().isEqualTo(expected2)
    }

}
