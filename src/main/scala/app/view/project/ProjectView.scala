package app.view.project

import app.util.Convertor._
import app.util.{Http, Store}
import app.util.Store.ProjectVar
import app.view.frame.Route
import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.dom
import org.scalajs.dom.raw.Event
import upickle.default.read
import scala.language.experimental.macros
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

object ProjectView
{
    val projectList = Vars.empty[ProjectVar]

    def AddorEditProject(p: ProjectVar) =
    {
        if (p.projId.value != -1)
            Store.currentProject = ProjectVar(p.projId, p.projName, p.projDesc)
        else
            Store.currentProject = ProjectVar(Var(-1), Var(""), Var(""))
        Route.path.value = "project_op"
    }

    def mounted =
    {
        projectList.value.clear()
        Http.get("/project/getProjectList").andThen {
            case Success(response) =>
                val str = response.responseText
                read[ProjectList](str).data.map(pro => projectList.value += ProjectVar(Var(pro.projId), Var(pro.projName), Var(pro.projDesc)))
            case Failure(exception) =>
                Route.path.value = "exception"
        }
    }

    @dom def render =
    {
        mounted
        <div>
            <div style="margin-top: 50px"></div>
            <div class="ui container">
                <div class="ui grid">
                    {for (p <- projectList) yield {
                    <div class="five wide column">
                        <div class="ui card">
                            <div class="content">
                                <div class="header">
                                    {p.projName.bind}
                                </div>
                            </div>
                            <div class="content">
                                <h4 class="ui sub header">
                                    {p.projDesc.bind}
                                </h4>
                            </div>
                            <div class="extra content">
                                <button class="ui primary button">进入项目</button>
                                <button class="ui  button" onclick={e: Event => AddorEditProject(p)}>修改项目</button>
                            </div>
                        </div>
                    </div>
                }}<div class="five wide column">
                    <div class="ui card">
                        <button class="green ui button" onclick={e: Event => AddorEditProject(ProjectVar(Var(-1),Var(""),Var("")))}>添加项目</button>
                    </div>
                </div>
                </div>
            </div>
        </div>
    }
}
