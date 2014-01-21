(ns playground.git)

(defn git [name]
  {:name name
   :head nil
   :last-commit-id -1})

(defn commit [repo message]
  (let
    [commit-id (inc (:last-commit-id repo))
     this-commit {:id commit-id
                  :message message
                  :parent (:head repo)}]
    {:name (:name repo)
     :last-commit-id commit-id
     :head this-commit}))

(defn inner-log [a-commit]
  (let [parent (:parent a-commit)]
    (if parent
      (conj (inner-log parent) a-commit)
      (list a-commit))))

(defn log [a-commit] (inner-log (:head repo)))

;; Poor-man's testing

(def repo (git "test"))
(def repo (commit repo "Inital commit"))
(def repo (commit repo "Change 1"))

(= (count (log repo)) 2) ;; Should have two commits
(= (:id (first (log repo))) 1) ;; Commit 1 should be first
(= (:id (second (log repo))) 0) ;; Commit 0 should be second