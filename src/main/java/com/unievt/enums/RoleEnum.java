package com.unievt.enums;

/**
 * Permission roles only.
 *
 * <p>Identity facts (étudiant / club president) are NOT roles — they are
 * derived from the data:
 * <ul>
 *   <li>"is an étudiant" → row exists in the {@code etudiant} table</li>
 *   <li>"is the president of a club" → {@code Club.president} FK points to this user</li>
 * </ul>
 *
 * <p>So a regular étudiant has {@code role = null}. An étudiant who is also
 * a doyen carries {@code role = DOYEN} on top of their étudiant data row.
 */
public enum RoleEnum {
    ADMIN,
    DOYEN,
    RESPONSABLE_EVENEMENTS
}
