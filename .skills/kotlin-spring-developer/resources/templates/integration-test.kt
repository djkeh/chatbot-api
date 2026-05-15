@DisplayName("[Integration] 예시 유즈케이스 통합 테스트")
@Transactional
@SpringBootTest
class ExampleIntegrationTest @Autowired constructor(
    private val sut: ExampleUseCase,
    private val exampleRepository: ExampleRepository,
) {

    @Test
    fun `비즈니스 로직을 실행하면, 저장소에 반영한다`() {
        // given
        val input = "data"

        // when
        sut.execute(input)

        // then
        val examples = exampleRepository.findAll()
        assertThat(examples)
            .hasSize(1)
            .first()
            .hasFieldOrPropertyWithValue("propertyName", "expectedValue")
    }

}
