package com.wuledi.common.util;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类（线程安全）
 * <p>提供常见格式的验证方法，所有正则表达式均已预编译</p>
 *
 * @author wuledi
 */
public final class RegexUtils {

    // 预编译所有正则表达式（提升性能）
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&_-]{6,20}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{3,14}$");

    // 私有构造方法防止实例化
    private RegexUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * 验证邮箱格式（支持常见格式）
     * <p>示例：user.name@example.com</p>
     *
     * @param email 待验证邮箱字符串
     * @return 符合格式返回 true，否则 false
     */
    public static boolean isNotEmailValid(String email) {
        return email == null || !EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证中国大陆手机号格式（11位）
     * <p>示例：13800138000</p>
     *
     * @param phone 待验证手机号字符串
     * @return 符合格式返回 true，否则 false
     */
    public static boolean isPhoneValid(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证密码格式（6-20位字母+数字组合）
     * <p>规则：</p>
     * <ul>
     *   <li>至少包含1个字母和1个数字</li>
     *   <li>允许特殊字符：@$!%*?&_-</li>
     * </ul>
     *
     * @param password 待验证密码字符串
     * @return 符合格式返回 true，否则 false
     */
    public static boolean isPasswordValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证用户名格式（5-15位字母开头+数字组合）
     * <p>规则：</p>
     * <ul>
     *   <li>以字母开头</li>
     *   <li>总长度5-15位（首字符后跟4-14位字母/数字）</li>
     *   <li>只允许字母和数字</li>
     * </ul>
     *
     * @param username 待验证用户名字符串
     * @return 符合格式返回 true，否则 false
     */
    public static boolean isUsernameValid(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
}