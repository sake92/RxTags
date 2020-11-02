package ba.sake.rxtags.example

import scalatags.JsDom.all._

object Ex2 {

  case class BlogPost(title: String, content: String)

  val blogPosts = List(
    BlogPost("Hello ScalaJS", "Lorem Ipsum ScalaJS.........."),
    BlogPost("Functional programming", "Lorem Ipsum FP.........."),
    BlogPost("Blockhain stuff", "Lorem Ipsum Blockhain..........")
  )

  def content(): Frag =
    div(
      h1("My awesome blog"),
      hr,
      blogPosts.map { post =>
        div(
          h3(post.title),
          post.content
        )
      }
    )
}
