package de.livepoll.api.controller.v1

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.MultipleChoiceItemAnswerDtoOut
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoOut
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
    private val testDataMultipleChoiceItem = getTestDataForMultipleChoice()
    private val assertDataMultipleChoiceItem = getAssertDataForMultipleChoice()

    @Before
    fun init() {
        // Setup fake function calls
        Mockito.`when`(pollItemRepository.findById(0)).thenReturn(Optional.of(testDataMultipleChoiceItem[0]))
        Mockito.`when`(pollItemRepository.findById(1)).thenReturn(Optional.of(testDataMultipleChoiceItem[1]))
        Mockito.`when`(multipleChoiceItemRepository.findById(0)).thenReturn(Optional.of(testDataMultipleChoiceItem[0]))
        Mockito.`when`(multipleChoiceItemRepository.findById(1)).thenReturn(Optional.of(testDataMultipleChoiceItem[1]))
    }


    // ----------------------------------------- Multiple Choice -------------------------------------------------------
    private fun getTestDataForMultipleChoice(): List<MultipleChoiceItem> {
        // Shell
        val multipleChoiceItem1 = MultipleChoiceItem(
            0,
            mockPoll,
            0,
            "Question1?",
            false,
            false,
            mutableListOf()
        )
        val multipleChoiceItem2 = MultipleChoiceItem(
            1,
            mockPoll,
            1,
            "Question2?",
            false,
            false,
            mutableListOf()
        )

        // Selection options
        val answer11 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option1-1", 0)
        val answer12 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option1-2", 0)
        val answer13 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option1-3", 0)
        val answer14 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option1-4", 0)
        multipleChoiceItem1.answers = listOf(answer11, answer12, answer13, answer14).toMutableList()

        val answer21 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option2-1", 0)
        val answer22 = MultipleChoiceItemAnswer(0, multipleChoiceItem1, "Option2-2", 0)
        multipleChoiceItem2.answers = listOf(answer21, answer22).toMutableList()

        return listOf(multipleChoiceItem1, multipleChoiceItem2)
    }

    private fun getAssertDataForMultipleChoice(): List<MultipleChoiceItemDtoOut> {
        // Selection options
        val answer11 = MultipleChoiceItemAnswerDtoOut(0, "Option1-1", 0)
        val answer12 = MultipleChoiceItemAnswerDtoOut(0, "Option1-2", 0)
        val answer13 = MultipleChoiceItemAnswerDtoOut(0, "Option1-3", 0)
        val answer14 = MultipleChoiceItemAnswerDtoOut(0, "Option1-4", 0)
        val answers1 = listOf(answer11, answer12, answer13, answer14)

        val answer21 = MultipleChoiceItemAnswerDtoOut(0, "Option2-1", 0)
        val answer22 = MultipleChoiceItemAnswerDtoOut(0, "Option2-2", 0)
        val answers2 = listOf(answer21, answer22)

        // Shell
        val multipleChoice1 = MultipleChoiceItemDtoOut(
            0,
            mockPoll.id,
            "Question1?",
            0,
            PollItemType.MULTIPLE_CHOICE.representation,
            answers1
        )
        val multipleChoice2 = MultipleChoiceItemDtoOut(
            1,
            mockPoll.id,
            "Question2?",
            1,
            PollItemType.MULTIPLE_CHOICE.representation,
            answers2
        )

        return listOf(multipleChoice1, multipleChoice2)
    }

    @Test
    @DisplayName("Get a single multiple choice item")
    fun testGetPollItemMultipleChoice() {
        val result = pollItemService.getPollItem(0)
        val expected = assertDataMultipleChoiceItem[0]
        assertThat(result).usingRecursiveComparison().isEqualTo(expected)
//        assertThat(result).isEqualToComparingFieldByFieldRecursively(assertDataMultipleChoiceItem.elementAt(1))
    }

}
