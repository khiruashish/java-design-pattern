package com.handbook.creational.prototype.saas;

/** Immutable leaf: safe to share between copies, no need to clone. */
public record Task(String title, String assigneeRole) {
}
