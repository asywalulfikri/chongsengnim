package sembako.sayunara.android.account

import org.junit.Assert
import org.junit.Test
import rk.emailvalidator.emailvalidator4j.EmailValidator
import sembako.sayunara.android.ui.component.account.login.LoginPresenter

class LoginTest  {


    @Test
    fun validEmailTest() {
        val input = "fikri@gmail.com"
        Assert.assertTrue(EmailValidator().isValid(input))
    }

    @Test
    fun validEmailWithSubDomain() {
        val input = "base@email.co.uk"
        Assert.assertTrue(EmailValidator().isValid(input))
    }

    @Test
    fun invalidEmailWithExtraCharacters() {
        val input = "base@email..co.uk"
        Assert.assertFalse(EmailValidator().isValid(input))
    }

    @Test
    fun invalidEmailWithNoUsername() {
        val input = "@email.co.uk"
        Assert.assertFalse(EmailValidator().isValid(input))
    }

    @Test
    fun invalidEmailWithNoInput() {
        Assert.assertFalse(EmailValidator().isValid(""))
    }


    @Test
    fun allValidLoginTest() {
        val presenter = LoginPresenter()
        val result: Boolean = presenter.checkData("sayunarasembako@gmail.com","123456")
        Assert.assertTrue(result)
    }

    @Test
    fun invalidEmailLoginTest() {
        val presenter = LoginPresenter()
        val result: Boolean = presenter.checkData("fikri@fupei...com","123456")
        Assert.assertFalse(result)
    }

    @Test
    fun passwordShortLoginTest() {
        val presenter = LoginPresenter()
        val result: Boolean = presenter.checkData("fikri@fupei..com","123")
        Assert.assertFalse(result)
    }




}