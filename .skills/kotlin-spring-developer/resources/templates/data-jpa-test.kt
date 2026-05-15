@DisplayName("[Repository] 예시 저장소 테스트")
@DataJpaTest
class ExamplePersistenceAdapterTest @Autowired constructor(
    private val exampleJpaRepository: ExampleJpaRepository,
    private val entityManager: TestEntityManager,
) {

    @Test
    fun `식별자가 주어지면, 엔티티를 반환한다`() {
        // given
        val entity = ExampleEntity().apply { propertyName = "test" }
        val id = exampleJpaRepository.save(entity).id
        entityManager.clear()

        // when
        val foundEntity = exampleJpaRepository.findById(id)

        // then
        assertThat(foundEntity)
            .get()
            .hasFieldOrPropertyWithValue("id", id)
            .hasFieldOrPropertyWithValue("propertyName", "test")
    }

}
