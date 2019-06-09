(ns cwiki-test.font-detection-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cwiki-mde.font-detection :as ft]))

;;; These tests are very system dependent. They may change depending on the
;;; operating system being used and the fonts actually installed on your
;;; system. These happen to work for me.

(deftest font-available?-test
  (testing "Correct detection of installed fonts."
    ; Safari says this is false
    (is (true? (ft/font-available? "Calibri")))
    (is (false? (ft/font-available? "Calibri Regular")))
    (is (true? (ft/font-available? "Arial")))
    (is (false? (ft/font-available? "Boojum")))
    (is (true? (ft/font-available? "Palatino")))
    (is (true? (ft/font-available? "Helvetica")))
    (is (true? (ft/font-available? "Helvetica Neue")))
    (is (false? (ft/font-available? "Helvetica Newish")))))

(deftest font-family->font-used-test
  (testing "Correct selection of and installed font from a font family.")
  (is (= "Arial" (ft/font-family->font-used "Century Gothic, Muli, Segoe UI, Arial, sans-serif")))
  (is (= "Palatino" (ft/font-family->font-used "Palatino, Palatino Linotype, Palatino LT STD, Book Antiqua, Georgia, serif")))
  ; Safari says this should be Menlo
  (is (= "Consolas" (ft/font-family->font-used "Consolas, Ubuntu Mono, Menlo, Monaco, Lucida Console,\n    Liberation Mono, DejaVu Sans Mono, Bitstream Vera Sans Mono,\n    Courier New, monospace, serif")))
  ; Safari says this should be "Helvetica Neue".
  (is (= "Calibri" (ft/font-family->font-used "Calibri, Segoe UI, Candara, Helvetica Neue,\n    Lucida Grande, Tahoma, Verdana, Helvetica, Arial, sans-serif"))))