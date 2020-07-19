package main.model

import com.fasterxml.jackson.annotation.JsonView
import main.util.Views
import main.util.Views.UserViews.UserComplete
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*

@Entity(name = "usr")
class User : UserDetails {

    @Id
    @JsonView(Views.Minimal::class)
    private var id: Long = 0

    @JsonView(Views.Minimal::class)
    private var username: String? = null

    @JsonView(UserComplete::class)
    private var login: String? = null

    @JsonView(UserComplete::class)
    private var password: String? = null

    @ElementCollection(targetClass = Roles::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(value = EnumType.STRING)
    @JsonView(Views.Minimal::class)
    private var roles: List<Roles>

    constructor(){
        roles = emptyList()
    }
    constructor(id: Long, username: String?, login: String?, password: String?, roles: List<Roles>) {
        this.id = id
        this.username = username
        this.login = login
        this.password = password
        this.roles = roles
    }

    constructor(roles: List<Roles>) {
        this.roles = roles
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val user = other as User
        return id == user.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles.toMutableList()
    }

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String? {
        return username
    }

    override fun getPassword(): String? {
        return password
    }

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    enum class Roles : GrantedAuthority {
        ADMIN, USER;

        override fun getAuthority(): String {
            return this.name
        }
    }
}