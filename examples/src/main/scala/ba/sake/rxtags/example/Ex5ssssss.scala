package ba.sake.rxtags.example

object Ex5ssssss

//      "ba.sake" %%% "hepek-components" % "0.8.5"
/*
import java.util.UUID

import org.scalajs.dom
import ba.sake.hepek.bootstrap3.BootstrapBundle
import ba.sake.hepek.bootstrap3.component.BootstrapFormComponents
import ba.sake.rxtags._
import scalatags.JsDom.all._

case class UserModel(username: String, email: String, work: Seq[WorkExperience] = Seq.empty)

case class WorkExperience(
    id: UUID = UUID.randomUUID(),
    company: String = "",
    position: String = "",
    summary: String = ""
)

object Ex5ssssss {
  val bundle = BootstrapBundle()
  val newForm = bundle.Form.withFormType(BootstrapFormComponents.Type.Horizontal())
  import newForm._, bundle.Classes._

  val user$ = Var(UserModel("", ""))

  def content(): Frag =
    div(
      form()(
        inputText(
          value := user$.map(_.username)
        )(_label = "Username"),
        inputEmail(
          value := user$.map(_.email)
        )(_label = "Email"),
        hr,
        h3("Work experience:"),
        user$.map { user =>
          user.work.map { wrk =>
            div(id := wrk.id.toString)(
              inputText(
                value := wrk.company,
                onkeyup := updateWorkExp(wrk.id, (we, v) => we.copy(company = v))
              )(_label = "Company name"),
              inputButton(onclick := removeWorkExp(wrk.id), btnDanger, btnSizeXs)(
                //frag(FA.times(), " Remove")
                "Remove"
              ),
              hr
            )

          }
        }.asFrag,
        inputButton(onclick := addWorkExp)("Add work experience")
      )
    ).render

  def addWorkExp: (dom.MouseEvent => Unit) =
    e => {
      user$.set(u => u.copy(work = u.work.appended(WorkExperience())))
    }

  def removeWorkExp(workExpId: UUID): (dom.MouseEvent => Unit) =
    e =>
      user$.set { user =>
        val newExps = user.work.filterNot(_.id == workExpId)
        val newwwww = user.copy(work = newExps)
        println("DEL: " + newwwww)
        newwwww
      }

  private def updateWorkExp(
      workExpId: UUID,
      f: (WorkExperience, String) => WorkExperience
  ): (dom.Event => Unit) =
    e =>
      user$.set { user =>
        val inputField = e.target.asInstanceOf[dom.html.Input]
        val idx = user.work.indexWhere(_.id == workExpId)
        val updatedWorkExp = f(user.work(idx), inputField.value)
        val newExps = user.work.updated(idx, updatedWorkExp)
        val newwwww = user.copy(work = newExps)
        println("UPDATED: " + newwwww)
        newwwww
      }
}
 */
