package ba.sake.rxtags.example

import scalatags.JsDom.all._

object Ex2 {

  case class BlogPost(title: String, content: String)

  val blogPosts = List(
    BlogPost("Hello ScalaJS", "Lorem Ipsum ScalaJS.........."),
    BlogPost("Functional programming", "Lorem Ipsum FP..........")
  )

  def content(): Frag =
    div(
      h2("Example 2"),
      blogPosts.map { post =>
        div(
          h4(post.title),
          post.content
        )
      }
    )
}
