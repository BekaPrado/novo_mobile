package com.example.app_journey.model


data class Ebook(
    val id_ebooks: Int,
    val titulo: String,
    val preco: Double,
    val descricao: String,
    val link_imagem: String,
    val link_arquivo_pdf: String?,
    val id_usuario: Int,
    val usuario: Usuario? = null,
    val categoriasEbooks: List<CategoriaEbook>? = null
)
// EbookRequest.kt
data class EbookRequest(
    val titulo: String,
    val preco: Double,
    val descricao: String,
    val link_imagem: String,
    val link_arquivo_pdf: String? = null,
    val id_usuario: Int,
    val categorias: List<Int>? = null
)

// CategoriaEbookRequest.kt
data class CategoriaEbookRequest(
    val id_ebooks: Int,
    val id_categoria: Int
)

// CategoriaEbook.kt
data class CategoriaEbook(
    val id_ebooks_categoria: Int,
    val id_ebooks: Int,
    val id_categoria: Int,
    val categoria: Categoria
)
// EbookResponseWrapper.kt
data class EbookResponseWrapper<T>(
    val status: Boolean,
    val status_code: Int,
    val itens: Int? = null,
    val ebooks: List<Ebook>? = null,
    val ebook: Ebook? = null,
    val ebooks_categorias: List<CategoriaEbook>? = null,
    val ebook_categoria: CategoriaEbook? = null
)
