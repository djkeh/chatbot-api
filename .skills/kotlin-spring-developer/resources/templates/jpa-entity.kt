@Table(name = "example_table")
@Entity
class ExampleTableEntity(
    @Column(nullable = false)
    var requiredProperty: String = "",
) : AuditingFields() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        private set

    var optionalProperty: String? = null

    override fun toString(): String {
        return "ExampleTableEntity(" +
            "id=$id, " +
            "requiredProperty=$requiredProperty, " +
            "optionalProperty=$optionalProperty, " +
            super.toString() +
            ")"
    }

}
