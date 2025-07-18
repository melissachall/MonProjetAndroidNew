import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import theme.BorderColor
import theme.CodeBackground
import theme.LinkColor
import theme.PrimaryColor
import theme.TextColor
import travelbuddy.composeapp.generated.resources.Res
import travelbuddy.composeapp.generated.resources.chat_bot
import travelbuddy.composeapp.generated.resources.gemini
import travelbuddy.composeapp.generated.resources.menu_profile
import travelbuddy.composeapp.generated.resources.profile_tab
import ui.app.toComposeImageBitmap
import ui.component.ShimmerAnimation
import ui.component.Tabx
import ui.viewmodel.HomeScreenModel
import util.BOTTOM_NAV_SPACE
import di.HomeScreenModelProviderr
import data.GeminiApi
import dev.shreyaspatil.ai.client.generativeai.type.GenerateContentResponse
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

data object GeminiTab : Tabx {
    override fun defaultTitle(): StringResource = Res.string.profile_tab
    override fun defaultIcon(): DrawableResource = Res.drawable.chat_bot

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.profile_tab)
            val icon = painterResource(Res.drawable.menu_profile)
            return TabOptions(
                index = 0u,
                title = title,
                icon = icon
            )
        }

    @Composable
    override fun Content() {
        val screenModel = HomeScreenModelProviderr.homeScreenModel
        val navigator = LocalNavigator.currentOrThrow
        GeminiScreenView(navigator = navigator, viewModel = screenModel)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GeminiScreenView(navigator: Navigator, viewModel: HomeScreenModel){
    viewModel.setBottomNavBarVisible(true)
    val api = remember { GeminiApi() }
    val coroutineScope = rememberCoroutineScope()
    var selectedImageData by remember { mutableStateOf<ByteArray?>(null) }
    var content by remember { mutableStateOf("") }
    var showProgress by remember { mutableStateOf(false) }
    var filePath by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    val navigateToGemini by viewModel.navigateToGemini.collectAsState()
    var prompt by remember { mutableStateOf("") }
    val canClearPrompt by remember { derivedStateOf { prompt.isNotBlank() } }

    // Utiliser LaunchedEffect pour la génération automatique quand navigateToGemini change
    LaunchedEffect(navigateToGemini) {
        if (navigateToGemini.first && navigateToGemini.second != null) {
            val customPrompt = """
                As a tourist, I want to explore and learn about a destination. Please provide comprehensive information about the following place: ${navigateToGemini.second?.title}.
                Include key details such as:
                - A brief overview of the place
                - Historical or cultural significance
                - Popular tourist attractions or landmarks
                - Best time to visit
                - Available activities
                - Images of the destination
                - Navigation routes or how to reach there from common locations
                Make the information engaging and easy to understand.
            """.trimIndent()
            println("prompt = $customPrompt")
            content = ""
            generateContentAsFlow(api, customPrompt, selectedImageData)
                .onStart { showProgress = true }
                .onCompletion {
                    showProgress = false
                    viewModel.navigateToGimini(navigateToGemini.copy(false))
                }
                .collect {
                    showProgress = false
                    println("response = ${it.text}")
                    content += it.text
                }
        }
    }

    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = BOTTOM_NAV_SPACE)) {
        val imagePickerLauncher = rememberFilePickerLauncher(PickerType.Image) { selectedImage ->
            coroutineScope.launch {
                val bytes = selectedImage?.readBytes()
                selectedImageData = bytes
                image = bytes?.toComposeImageBitmap()
                filePath = selectedImage?.path ?: ""
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth().padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 26.dp),
                text = stringResource(Res.string.gemini),
                color = TextColor,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            ) {
                OutlinedTextField(
                    value = navigateToGemini.second?.title ?: prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .defaultMinSize(minHeight = 52.dp),
                    label = {
                        Text(
                            text =  "Search",
                            color = TextColor,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    trailingIcon = {
                        if (canClearPrompt) {
                            IconButton(
                                onClick = { prompt = "" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = TextColor,
                        unfocusedLabelColor = TextColor,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = TextColor,
                        cursorColor = PrimaryColor,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor
                    )
                )

                OutlinedButton(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            coroutineScope.launch {
                                println("prompt = $prompt")
                                content = ""
                                generateContentAsFlow(api, prompt, selectedImageData)
                                    .onStart { showProgress = true }
                                    .onCompletion { showProgress = false }
                                    .collect {
                                        showProgress = false
                                        println("response = ${it.text}")
                                        content += it.text
                                    }
                            }
                        }
                    },
                    enabled = prompt.isNotBlank(),
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text =  "Submit",
                        color = TextColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                OutlinedButton(
                    onClick = { imagePickerLauncher.launch() },
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text =  "Select Image",
                        color = TextColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            image?.let { imageBitmap ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = BitmapPainter(imageBitmap),
                        contentDescription = "search_image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            if (showProgress) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        repeat(5) {
                            ShimmerAnimation()
                        }
                    }
                }
            } else {
                SelectionContainer {
                    com.mikepenz.markdown.m3.Markdown(
                        modifier = Modifier.fillMaxSize(),
                        content = content,
                        colors = com.mikepenz.markdown.m3.markdownColor(
                            text = TextColor,
                            inlineCodeText = PrimaryColor,
                            dividerColor = BorderColor,
                            codeText = PrimaryColor,
                            linkText = LinkColor,
                            codeBackground = CodeBackground,
                            inlineCodeBackground = CodeBackground
                        )
                    )
                }
            }
        }
    }
}

fun generateContentAsFlow(
    api: GeminiApi,
    prompt: String,
    imageData: ByteArray? = null
): Flow<GenerateContentResponse> = imageData?.let { imageByteArray ->
    api.generateContent(prompt, imageByteArray)
} ?: run {
    api.generateContent(prompt)
}