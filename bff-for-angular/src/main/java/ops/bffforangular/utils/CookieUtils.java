package ops.bffforangular.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    @Value("${cookie.domain}")
    private String cookieDomain;
    public HttpCookie createCookie(String name, String value, int durationInSeconds) { // jwt - значение для кука
        return ResponseCookie
                // настройки кука
                .from(name, value) // название и значение кука
                .maxAge(durationInSeconds) // 86400 сек = 1 сутки
                .sameSite("Strict") // запрет на отправку кука на сервер, если выполняется межсайтовый запрос (доп. защита от CSRF атак) - кук будет отправляться только если пользователь сам набрал URL в адресной строке
                .httpOnly(true) // кук будет доступен для считывания только на сервере (на клиенте НЕ будет доступен с помощью JavaScript - тем самым защищаемся от XSS атак)
                //.secure(true) // кук будет передаваться браузером на backend только если канал будет защищен (https)
                .domain(cookieDomain) // для какого домена действует кук (перед отправкой запроса на backend - браузер "смотрит" на какой домен он отправляется - и если совпадает со значением из кука - тогда прикрепляет кук к запросу)
                .path("/") // кук будет доступен для всех URL

                // создание объекта
                .build();

		/* примечание: все настройки кука (domain, path и пр.) - влияют на то, будет ли браузер отправлять их при запросе.

		Браузер сверяет URL запроса (который набрали в адресной строке или любой ajax запрос с формы) с параметрами кука.
		И если есть хотя бы одно несовпадение (например domain или path) - кук отправлен не будет.

		*/
    }
    public HttpCookie deleteCookie(String name) {
        return ResponseCookie.
                from(name, "") // пустое значение
                .maxAge(0) // кук с нулевым сроком действия браузер удалит автоматически
                .sameSite("Strict") // или None
                .httpOnly(true)
                //.secure(true)
                .domain(cookieDomain)
                .path("/")
                .build();

    }
}
