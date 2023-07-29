/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    data class Question(
        val text: String,
        val answers: List<String>
    )

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (Or better yet, don't define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf(
        Question(
            text = "Aureolin is a shade of what color?",
            answers = listOf("Yellow", "Green", "Red", "Blue")
        ),
        Question(
            text = "Which planet in the milky way is the hottest?",
            answers = listOf("Venus", "Mars", "Mercury", "Jupiter")
        ),
        Question(
            text = "What is the 4th letter of the greek alphabet?",
            answers = listOf("Delta", "Alpha", "Beta", "Omega")
        ),
        Question(
            text = "What company was initially known as Blue Ribbon Sports?",
            answers = listOf("Nike", "Puma", "Adidas", "Vans")
        ),
        Question(
            text = "What is the largest spanish-speaking city?",
            answers = listOf("Mexico City", "Madrid", "Buenos Aires", "Barcelona")
        ),
        Question(
            text = "In What country is the chernobyl nuclear plant located?",
            answers = listOf("Ukraine", "Russia", "Slovakia", "Slovenia")
        ),
        Question(
            text = "In What country was Elon Musk born?",
            answers = listOf("South Africa", "United States Of America", "Sweden", "Britain")
        ),
        Question(
            text = "How many hearts does an Octopus have?",
            answers = listOf("3", "2", "8", "6")
        ),
        Question(
            text = "Where is the strongest human muscle located?",
            answers = listOf("jaw", "hip", "shoulder", "backbone")
        ),
        Question(
            text = "What is the capital of Canada?",
            answers = listOf("Ottawa", "Toronto", "Vancouver", "Montreal")
        ),
        Question(
            text = "Pink Ladies and Granny Smiths are types of what fruit?",
            answers = listOf("Apple", "Berry", "Orange", "Mango")
        ),
        Question(
            text = "What color are Mickey Mouse shoes?",
            answers = listOf("Yellow", "Black", "White", "Red")
        ),
        Question(
            text = "What country drinks the most coffee?",
            answers = listOf("Finland", "Britain", "Spain", "Portugal")
        ),
        Question(
            text = "What colors is the flag of the United Nations?",
            answers = listOf("Blue and White", "White and Green", "Green and Blue", "Black and White")
        ),
        Question(
            text = "What is acrophobia a fear of?",
            answers = listOf("Flying", "Swimming", "Cycling", "Sleeping")
        ),
        Question(
            text = "Which planet has the most moons?",
            answers = listOf("Saturn", "Pluto", "Jupiter", "Mars")
        ),
        Question(
            text = "What sports car company manufactures the 911?",
            answers = listOf("Porsche", "Buggati", "Ferrari", "BMW")
        )
    )


    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = Math.min((questions.size + 1) / 2, 3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
            inflater, R.layout.fragment_game, container, false
        )

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
                        view.findNavController().navigate(
                            GameFragmentDirections.actionGameFragmentToGameWonFragment(
                                numQuestions,
                                questionIndex
                            )
                        )
                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
                    view.findNavController()
                        .navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
                }
            }
        }
        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }
}
