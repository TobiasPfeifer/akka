package akka.http.scaladsl.server.directives

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, Uri }
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.{ Sink, Source }

trait ReverseProxyDirective {

  // how to obtain the ActorSystem used to forward the request?
  def reverseProxy(host: String, port: Int)(implicit system: ActorSystem): Route = { ctx ⇒
    // adjust request uri
    // add ´X-Forwarded-For´ and ´Via´ Header
    val proxiedRequest: HttpRequest = ctx.request.withUri(Uri(???)).withEffectiveUri(???).addHeader(???)

    implicit val materializer: Materializer = ctx.materializer
    val connection = Http().outgoingConnection(host, port)
    val responseFuture = Source.single(proxiedRequest).via(connection).runWith(Sink.head)

    // should we rather use singleRequest() which uses a connection pool?
    // val responseFuture: Future[HttpResponse] = Http().singleRequest(proxiedRequest)

    ctx.complete(responseFuture)
  }
}

object ReverseProxyDirective extends ReverseProxyDirective