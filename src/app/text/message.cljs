(ns app.text.message
  (:require
   [cljs-bean.core :refer [bean ->clj ->js]]
   [applied-science.js-interop :as j]))


(def labels
  {:question
    {:title-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠪᠠᠨ ᠣᠷᠤᠭᠤᠯᠤᠭᠠᠳ ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠡᠮᠳᠡᠭ ᠶᠢᠡᠷ ᠲᠡᠭᠦᠰᠭᠡᠨ ᠡ"
     :content-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠠᠯ ᠠ ᠪᠡᠷ ᠨᠡᠮᠡᠯᠲᠡ ᠲᠠᠢᠯᠪᠦᠷᠢ ᠵᠢ ᠡᠨᠳᠡ ᠣᠷᠤᠭᠤᠯᠤᠨ ᠠ᠂ ᠲᠠ ᠬᠠᠷᠢᠭᠤᠯᠲᠠ ᠵᠢ ᠢᠯᠡᠭᠦᠦ ᠬᠤᠷᠳᠤᠨ ᠣᠯᠬᠤ ᠪᠣᠯᠤᠮᠵᠢᠲᠠᠢ(ᠰᠤᠩᠭᠤᠨ ᠲᠠᠭᠯᠠᠬᠤ)"}})