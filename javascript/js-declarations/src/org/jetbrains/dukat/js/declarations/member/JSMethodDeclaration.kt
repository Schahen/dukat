package org.jetbrains.dukat.js.declarations.member

import org.jetbrains.dukat.js.declarations.JSDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration

data class JSMethodDeclaration(
        val function: JSFunctionDeclaration,

        val static: Boolean
) : JSDeclaration