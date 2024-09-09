package com.swm_standard.phote.entity

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuestionSetTest {
    private val fixtureMonkey: FixtureMonkey =
        FixtureMonkey
            .builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build()

    @Test
    fun `questionSet의 순서를 변경하는데 성공한다`() {
        val questionSet = fixtureMonkey.giveMeOne(QuestionSet::class.java)
        val newSequence = 4

        questionSet.updateSequence(newSequence)

        assertEquals(newSequence, questionSet.sequence)
    }

    @Test
    fun `questionSet을 생성하는데 성공한다`() {
        val question = createQuestion()
        val workbook = fixtureMonkey.giveMeOne(Workbook::class.java)
        val newSequence = 1

        val questionSet =
            QuestionSet.createQuestionSet(
                question = question,
                workbook = workbook,
                nextSequence = newSequence,
            )

        assertEquals(newSequence, questionSet.sequence)
        assertEquals(workbook, questionSet.workbook)
        assertEquals(question, questionSet.question)
    }

    private fun createQuestion(): Question =
        Question(
            member = Member("phote", "phote@test.com", "image", Provider.KAKAO),
            statement = "모든 각이 동일한 삼각형은?",
            image = "http://example.com/image.jpg",
            answer = "정삼각형",
            category = Category.ESSAY,
            memo = "삼각형 내각의 합은 180도이다.",
        )
}
