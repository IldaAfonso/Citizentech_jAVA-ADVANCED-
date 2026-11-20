package br.fiap.citizentech.exception;

import java.time.Instant;
import java.util.List;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static ResponseEntity<Object> montar(HttpStatus status, String mensagem, List<String> detalhes) {
        ErroResposta corpo = new ErroResposta(Instant.now(), status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(corpo);
    }

    // 400: Bean Validation (@Valid)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos.", detalhes);
    }

    // 400: JSON malformado ou valor de enum inexistente
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou ilegível.", List.of());
    }

    // 400: parâmetro com tipo errado (ex.: ?tipo=XXX)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return montar(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'.", List.of());
    }

    // 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> naoEncontrado(RecursoNaoEncontradoException ex) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    // 409: regra de negócio ou violação de integridade
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> regraNegocio(RegraNegocioException ex) {
        return montar(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> integridade(DataIntegrityViolationException ex) {
        return montar(HttpStatus.CONFLICT, "Operação viola uma restrição do banco de dados.", List.of());
    }

    // 401
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Object> credenciais(CredenciaisInvalidasException ex) {
        return montar(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of());
    }

    // 400: ordenação por campo inexistente (ex.: ?sort=xyz)
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> campoInvalido(PropertyReferenceException ex) {
        return montar(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido: " + ex.getPropertyName(), List.of());
    }

    // 500: qualquer outro erro (sem expor detalhes internos)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> inesperado(Exception ex) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado.", List.of());
    }
}
