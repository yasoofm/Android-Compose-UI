package com.example.androidcomposeui

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    var stateIndex = mutableStateOf(0)
    var index = mutableStateOf(0)
    var answerIndex = mutableStateOf(0)
    var score = mutableStateOf(0)
    var answer = mutableStateOf(false)
    var choice = mutableStateOf(false)
    lateinit var mediaPlayer: MediaPlayer
    lateinit var questions: ArrayList<String>
    val answers: ArrayList<Boolean> = arrayListOf(true, false, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questions = resources.getStringArray(R.array.questions).toCollection(ArrayList<String>())
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = questions.get(answerIndex.value), fontSize = 45.sp)
                        AnswerText(
                            index = index.value,
                            stateIndex = stateIndex.value,
                            answer = answer.value,
                            end = answers.size,
                            score = score.value,
                            context = LocalContext
                        )
                        Buttons(
                            stateIndex = stateIndex,
                            index = index,
                            answerIndex = answerIndex,
                            score = score,
                            choice = choice,
                            checkAnswer = { checkAnswer() },
                            answer = answer,
                            end = answers.size,
                            context = LocalContext,
                            playSound = { playSound(applicationContext) }
                        )
                    }
                }
            }
        }
    }

    fun playSound(context: Context) {
        if (answer.value) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ding)
            mediaPlayer.start()
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.beeps)
            mediaPlayer.start()
        }

    }

    fun checkAnswer() {
        if (choice.value == answers.get(answerIndex.value)) {
            answer.value = true
            index.value++
        } else {
            answer.value = false
            index.value++
        }
    }
}

@Composable
fun AnswerText(
    modifier: Modifier = Modifier,
    index: Int,
    stateIndex: Int,
    answer: Boolean,
    end: Int,
    score: Int,
    context: ProvidableCompositionLocal<Context>
) {
    if (index % 2 == 1 && answer) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.correct_answer),
                contentDescription = "correct answer"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = getString(context.current, R.string.correct),
                fontSize = 25.sp
            )
        }
    } else if (index % 2 == 1 && !answer) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.wrong_answer),
                contentDescription = "wrong answer"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = getString(context.current, R.string.incorrect),
                fontSize = 25.sp
            )
        }
    } else if (stateIndex == end)
        Text(text = "Score: $score", fontSize = 35.sp)
}

@Composable
fun RowButtons(
    trueOnClick: () -> Unit,
    falseOnClick: () -> Unit,
    modifier: Modifier = Modifier,
    context: ProvidableCompositionLocal<Context>
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = trueOnClick, modifier = Modifier.width(180.dp)) {
            Text(text = getString(context.current, R.string.trueText), fontSize = 30.sp)
        }
        Button(onClick = falseOnClick, modifier = Modifier.width(180.dp)) {
            Text(text = getString(context.current, R.string.falseText), fontSize = 30.sp)
        }
    }
}

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    stateIndex: MutableState<Int>,
    index: MutableState<Int>,
    answerIndex: MutableState<Int>,
    score: MutableState<Int>,
    choice: MutableState<Boolean>,
    checkAnswer: () -> Unit,
    answer: MutableState<Boolean>, end: Int,
    context: ProvidableCompositionLocal<Context>,
    playSound: () -> Unit
) {

    if (stateIndex.value == end)
        Button(
            modifier = Modifier.width(300.dp),
            onClick = {
                index.value = 0
                stateIndex.value = 0
                answerIndex.value = 0
                score.value = 0
            }) {
            Text(text = getString(context.current, R.string.reset), fontSize = 30.sp)
        }
    else if (index.value % 2 == 0)
        RowButtons(
            trueOnClick = {
                choice.value = true
                checkAnswer()
                playSound()
            },
            falseOnClick = {
                choice.value = false
                checkAnswer()
                playSound()
            },
            context = context,
        )
    else
        Button(
            modifier = Modifier.width(300.dp),
            onClick = {
                if (answer.value) {
                    if (answerIndex.value != end - 1)
                        answerIndex.value++
                    score.value++
                    stateIndex.value++
                    index.value++
                } else {
                    if (answerIndex.value != end - 1)
                        answerIndex.value++
                    index.value++
                    stateIndex.value++
                }
            }) {
            Text(text = getString(context.current, R.string.next), fontSize = 30.sp)
        }
}
