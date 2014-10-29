###Parallel Binary Search Tree and Traversals

####Implementation of three types of concurrent operations on a BST and their performance analysis:

1. Read-Write locks enabled parallel search, but not insert/delete (write operations) in parallel or concurrently with a search operation.
2. Copy-on-write mechanism, using lazy copy, allowed concurrent search and insert/delete operations, allowing access to an old version of the datastructure for search operation.
3. Fine-grained locking enabled concurrent search as well as insert/delete operation in parallel

####Design and Implementation of a parallel "lock free" algorithm(no starvation) for pre-order depth first traversal of a binary tree. The solution is generic and can be extended easily for an n-ary tree.
