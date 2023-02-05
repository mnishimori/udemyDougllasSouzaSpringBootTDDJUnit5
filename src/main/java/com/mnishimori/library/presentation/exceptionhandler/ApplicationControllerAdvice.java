package com.mnishimori.library.presentation.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mnishimori.library.exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApplicationControllerAdvice extends ResponseEntityExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  @ExceptionHandler({MaxUploadSizeExceededException.class})
  public ResponseEntity<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex,
      WebRequest request) {
    HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;

    String detail = String.format(
        "O tamanho do arquivo que você está tentando fazer upload excedeu o tamanho máximo permitido de 10MB.",
        ex.getMessage());

    return handleExceptionInternal(ex, detail, new HttpHeaders(), status, request);
  }


  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    String detail = String.format("O recurso %s não foi encontrado.",
        ex.getRequestURL());

    return handleExceptionInternal(ex, detail,
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }


  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    if (ex instanceof MethodArgumentTypeMismatchException) {
      return handleMethodArgumentTypeMismatch(
          (MethodArgumentTypeMismatchException) ex, headers, status, request);
    }

    return super.handleTypeMismatch(ex, headers, status, request);
  }

  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  public ResponseEntity<Object> handleValidationInternal(Exception ex, BindingResult bindingResult,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    List<ApiError.Object> problemObjects = bindingResult.getAllErrors()
        .stream()
        .map(objectError -> {
          String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

          String name = objectError.getObjectName();

          if (objectError instanceof FieldError) {
            name = ((FieldError) objectError).getField();
          }

          return ApiError.Object.builder()
              .name(name)
              .userMessage(message)
              .build();
        })
        .collect(Collectors.toList());

    String userMessage = getErrorBeanValidationMessage(problemObjects);

    ApiError apiError = this.createApiError(status, userMessage);

    return handleExceptionInternal(ex, apiError, headers, status, request);
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    String detail = "Erro interno no servidor. Por favor, comunique o administrador do sistema.";

    return handleExceptionInternal(ex, detail, new HttpHeaders(), status, request);
  }


  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, @Nullable Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    body = this.generateBody(body, status);

    return super.handleExceptionInternal(ex, body, headers, status, request);
  }


  private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    String detail = String.format("O parâmetro '%s' recebeu '%s', "
            + "valor inválido. Corrija para o parâmetro correto (%s).",
        ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

    return handleExceptionInternal(ex, detail, headers, status, request);
  }


  private ResponseEntity<Object> handleInvalidFormatException(
      InvalidFormatException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    String path = ex.getPath().stream()
        .map(ref -> ref.getFieldName())
        .collect(Collectors.joining("."));

    String detail = String.format("O campo '%s' recebeu o valor '%s', "
            + "inválido. Corrija com o valor no formato correto (%s).",
        path, ex.getValue(), ex.getTargetType().getSimpleName());

    return handleExceptionInternal(ex, detail, headers, status, request);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> catchNegocioException(BusinessException e,
      WebRequest request){

    HttpStatus status = HttpStatus.BAD_REQUEST;

    return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(),
        status, request);
  }

  private String getErrorBeanValidationMessage(List<ApiError.Object> problemObjects) {
    String userMessage = "";
    String userMessages = "";

    if (problemObjects != null && problemObjects.size() > 0) {
      userMessages = problemObjects
          .stream()
          .filter(m -> m.getUserMessage() != null && !m.getUserMessage().trim().isEmpty())
          .map(m -> m.getUserMessage().trim())
          .collect(Collectors.joining(", "));
    }

    if (userMessages != "") {
      userMessage = userMessages;
    } else {
      userMessage = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente";
    }

    return userMessage;
  }


  private Object generateBody(Object body, HttpStatus status) {
    if (body == null || (body != null && body instanceof String)) {
      String exceptionMessage = "";

      if (body == null) {
        exceptionMessage = status.getReasonPhrase();
      } else {
        exceptionMessage = (String) body;
      }

      body = this.createApiError(status, exceptionMessage);
    }
    return body;
  }


  private ApiError createApiError(HttpStatus status, String detail) {

    return ApiError.builder()
        .status_code(status.value())
        .message(detail)
        .build();
  }
}
