@DisplayName("[Controller] 예시 컨트롤러 테스트")
@WebMvcTest(ExampleController::class)
class ExampleControllerTest @Autowired constructor(
    private val mockMvc: MockMvcTester,
    @MockkBean private val exampleUseCase: ExampleUseCase,
) {

    @Test
    fun `요청을 보내면, 200 OK를 응답한다`() {
        // given
        every { exampleUseCase.execute(any()) } returns ExampleResponse(data = "success")

        // when & then
        assertThat(mockMvc.get()
            .uri("/api/example")
            .contentType(MediaType.APPLICATION_JSON)
        )
            .hasStatusOk()
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .isLenientlyEqualTo(
                """
                {
                    "data": "success"
                }
                """.trimIndent()
            )
        verify { exampleUseCase.execute(any()) }
    }

}
