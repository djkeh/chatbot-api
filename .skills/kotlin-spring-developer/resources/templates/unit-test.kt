@DisplayName("[Service] 예시 서비스 테스트")
@ExtendWith(MockKExtension::class)
class ExampleServiceTest(
    @MockK private val dependencyPort1: DependencyPort1,
    @MockK private val dependencyPort2: DependencyPort2,
    @MockK private val unusedPort: UnusedPort,
) {

    @InjectMockKs
    private lateinit var sut: ExampleService

    @Test
    fun `입력값이 주어지면, 결과를 반환한다`() {
        // given
        val input = "input"
        val expected = "expected"
        every { dependencyPort1.doSomething(input) } returns expected
        justRun { dependencyPort2.doSomething(any()) }

        // when
        val result = sut.execute(input)

        // then
        assertThat(result)
            .hasFieldOrPropertyWithValue("property", expected)
        verify {
            dependencyPort1.doSomething(input)
            dependencyPort2.doSomething(any())
            unusedPort wasNot Called
        }
    }

    @Test
    fun `잘못된 입력값이 주어지면, 예외를 던진다`() {
        // given
        val invalidInput = "wrong-input"
        every { dependencyPort1.doSomething(invalidInput) } throws IllegalArgumentException("잘못된 입력값입니다.")

        // when
        val t = catchThrowable { sut.execute(invalidInput) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("잘못된 입력값입니다.")
        verify {
            dependencyPort1.doSomething(invalidInput)
            dependencyPort2 wasNot Called
            unusedPort wasNot Called
        }
    }

}
