package ba.sake.rxtags.example

import scalatags.JsDom.all._

// scalatags list
object Ex2 extends Example {

  case class BlogPost(title: String, content: String)

  val blogPosts = List(
    BlogPost("Hello ScalaJS", "Lorem Ipsum ScalaJS.........."),
    BlogPost("Functional programming", "Lorem Ipsum FP..........")
  )

  def content = div(
    blogPosts.map { post =>
      div(
        h4(post.title),
        post.content
      )
    }
  )
}
