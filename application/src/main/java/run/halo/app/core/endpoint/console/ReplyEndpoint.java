package run.halo.app.core.endpoint.console;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;

import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.content.comment.ListedReply;
import run.halo.app.content.comment.ReplyQuery;
import run.halo.app.content.comment.ReplyService;
import run.halo.app.core.extension.content.Reply;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.ListResult;

/**
 * Endpoint for managing {@link Reply}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
public class ReplyEndpoint implements CustomEndpoint {

    private final ReplyService replyService;

    public ReplyEndpoint(ReplyService replyService) {
        this.replyService = replyService;
    }

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        var tag = "ReplyV1alpha1Console";
        return SpringdocRouteBuilder.route()
            .GET("replies", this::listReplies, builder -> {
                    builder.operationId("ListReplies")
                        .description("List replies.")
                        .tag(tag)
                        .response(responseBuilder()
                            .implementation(ListResult.generateGenericClass(ListedReply.class))
                        );
                    ReplyQuery.buildParameters(builder);
                }
            )
            .build();
    }

    Mono<ServerResponse> listReplies(ServerRequest request) {
        ReplyQuery replyQuery = new ReplyQuery(request.exchange());
        return replyService.list(replyQuery)
            .flatMap(listedReplies -> ServerResponse.ok().bodyValue(listedReplies));
    }
}
